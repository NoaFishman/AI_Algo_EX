import org.w3c.dom.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static java.util.Collections.sort;

public class EX1 {

    static int add = 0;
    static int multi = 0;

    public static void main(String[] args) throws Exception {
        String line;
        BayesianNet net = new BayesianNet();
        String filename = "input2.txt";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            if ((line = br.readLine()) != null) {
                Document doc = BayesianParser.parseXML(line);
                net = BayesianParser.parse(doc);
                net.setParentsOut();
                net.setCpt();
                net.setKids();
                //System.out.println(net);
            }
            while((line = br.readLine()) != null){
                if(line.charAt(0) == 'P'){
                    // variable elimination
                    String[] s = line.split("\\)");
                    String[] hiddens = s[1].split("-");
                    for(int i=0; i<hiddens.length; i++){
                        if (!hiddens[i].isEmpty() && hiddens[i].charAt(0) == ' ') {
                            hiddens[i] = hiddens[i].substring(1);
                        }
                    }
                    String[] e = s[0].split("\\|");
                    ArrayList<String> evi = new ArrayList<>();
                    if(e.length > 1){
                        // else?????????????????
                        String[] evidence = e[1].split(",");
                        for (int i = 0; i < evidence.length; i++) {
                            evi.add(evidence[i]);
                        }
                    }
                    String[] q = e[0].split("\\(");
                    variableElimination(hiddens, net, evi, q[1]);
                    //String[] query = q[1].split("=");
                    // to realized how to save the Q
                }
                else{
                    // Bayes ball read the line
                    String[] bayes = line.split("\\|");
                    String[] variables = bayes[0].split("-");
                    if(bayes.length >1){
                        String[] evidence = bayes[1].split(",");
                        for(int i = 0; i < evidence.length; i++){
                            String[] values = evidence[i].split("=");
                            net.getNode(values[0]).setEvidence(true);
                        }
                    }
                    bayesBall(net, variables[0], variables[1]);
                }

            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void variableElimination(String[] hidden, BayesianNet net, ArrayList<String> evi, String query){

        List<String> parents = new ArrayList<>(net.allParents(query.split("=")[0]));
        parents.add(query.split("=")[0]);

//        List<String> kids = new ArrayList<>(net.allKids(query.split("=")[0]));
//        for(String kid: kids){
//            parents.add(kid);
//        }

        // create factors array
        ArrayList<Factor> factors = new ArrayList<>();
        for(String name: net.getNodes().keySet()){
            if(parents.contains(name)){
                factors.add(new Factor(net.getNode(name).getCpt()));
            }
        }



        ArrayList<String> evidence = new ArrayList<>();
        ArrayList<String> evidenceVal = new ArrayList<>();
        for(int i=0; i< evi.size(); i++){
            String[] e = evi.get(i).split("=");
            evidence.add(e[0]);
            evidenceVal.add(e[1]);
        }

        for(String e: evidence){
            if(!parents.contains(e)){
                parents.add(e);
                factors.add(new Factor(net.getNode(e).getCpt()));
            }
        }

        for(String e: evidence){
            List<String> parentsE = new ArrayList<>(net.allParents(e));
            for(String p: parentsE){
                if(!parents.contains(p)){
                    parents.add(p);
                    factors.add(new Factor(net.getNode(p).getCpt()));
                }
            }
//            List<String> kidsE = new ArrayList<>(net.allKids(e));
//            for(String kid: kidsE){
//                if(!parents.contains(kid)){
//                    parents.add(kid);
//                    factors.add(new Factor(net.getNode(kid).getCpt()));
//                }
//            }
        }


        // remove the irrelevant rows and the evidence
        for (Factor factor : factors) {
            factor.removeIrrelevant(evi);
            factor.removeEvi(evi);
        }

        List<Factor> toRemove = new ArrayList<>();
        for (Factor factor : factors) {
            boolean flag = true;
            for(String parent: parents){
                if(factor.containsVar(parent)){
                    flag = false;
                }
            }
            if(flag){
                toRemove.add(factor);
            }
        }
        for(Factor factor: toRemove){
            factors.remove(factor);
        }



        // check if already exist cpt that get thi answer
        boolean flag2 = true;
        for(String parent: net.getNode(query.split("=")[0]).getParents()){
            if(!evidence.contains(parent)){
                flag2 = false;
            }
        }
        for(String e: evidence){
            if(!net.getNode(query.split("=")[0]).getParents().contains(e)){
                flag2 = false;
            }
        }
        if (flag2){
            Map<String, String> key = new HashMap<>();
            key.put(query.split("=")[0], query.split("=")[1]);
            for(int i=0; i<evidence.size(); i++){
                key.put(evidence.get(i), evidenceVal.get(i));
            }

            Double win = net.getNode(query.split("=")[0]).getCpt().get(key);
            System.out.println(win+","+factors.get(0).getAdd()+","+factors.get(0).getMulti());
            factors.get(0).setAddMulti();
            return;
        }

        List<Factor> toRemove2 = new ArrayList<>();
        for(Factor factor: factors){
            if(factor.cpt.size() == 1){
                toRemove2.add(factor);
            }
        }
        for(Factor factor: toRemove2){
            factors.remove(factor);
        }

        // sorting all the factors by size
        sort(factors);

        List<String> hiddens = new ArrayList<>(Arrays.stream(hidden).toList());

//        for (Factor factor: factors){
//            System.out.println(factor);
//        }

        // run until there is no more hidden to eliminate
        while(!hiddens.isEmpty()){
            int sum =0;
            int i=0;
            int index1=0, index2=0;
            while(i < factors.size()) {
                while (sum < 2 && i < factors.size()) {
                    if (factors.get(i).containsVar(hiddens.get(0))) {
                        sum++;
                        if (sum == 1) {
                            index1 = i;
                        } else if (sum == 2) {
                            index2 = i;

                        }
                    }
                    i++;
                }
                if(sum == 2){
                    Factor f = new Factor(factors.get(index1).join(factors.get(index2)));
                    factors.remove(factors.get(index2));
                    factors.remove(factors.get(index1));
                    factors.add(f);
                    sort(factors); //to add after the problem solved
                    sum = 0;
                    i=0;
                }
            }
            if(sum == 1){
                factors.get(index1).eliminate(hiddens.get(0));
            }
            hiddens.remove(0);
        }

//        for (Factor factor: factors){
//            System.out.println(factor);
//        }

        System.out.println("----"+factors.get(0).getAdd()+","+factors.get(0).getMulti());
        while(factors.size() > 1){
            Factor f = new Factor(factors.get(0).join(factors.get(1)));
            factors.remove(1);
            factors.remove(0);
            factors.add(f);
        }

        factors.get(0).normalize();
        String ans = factors.get(0).getValue(query);
        System.out.println(ans+","+factors.get(0).getAdd()+","+factors.get(0).getMulti());
        factors.get(0).setAddMulti();


    }




    public static void bayesBall(BayesianNet net , String source, String dest ){
        if(bayesBallRec(net, source,dest, source, true)){
            System.out.println("no");
        }
        else{
            System.out.println("yes");
        }
        // set the net ready for the next Query
        for(String node:net.getNodes().keySet() ){
            net.getNode(node).setEvidence(false);
            net.getNode(node).setColor(false);
        }

    }

    public static boolean bayesBallRec(BayesianNet net , String sorce, String dest, String curr, boolean fromParents){
        if(net.getNode(curr).isColor() && fromParents){
            return false;
        }
        net.getNode(curr).setColor(true); // coloring this node
        if(net.getNode(dest) == net.getNode(curr)){ // if this is the dest so dependent
            return true;
        }
        if(net.getNode(curr).isEvidence() && fromParents){// if evidence so go up to the parents
            if(net.getNode(curr).getParents() == null){ // if no parents so it is ded end
                return false;
            }
            for(String parent: net.getNode(curr).getParents()){// for all the parents if we got the dest so they dependent
                if(bayesBallRec(net, sorce, dest, parent, false)){
                    return true;
                }
            }
        }
        else if(net.getNode(curr).isEvidence() && !fromParents){ // if came from child to evidence there is no way
            return false;
        }
        else{
            if(fromParents){
                if(net.getNode(curr).getChildrens() == null){// ded end
                    return false;
                }
                for(String kid: net.getNode(curr).getChildrens()){
                    if(bayesBallRec(net, sorce, dest, kid, true)){
                        return true;
                    }
                }
            }
            else{
                if(net.getNode(curr).getChildrens() == null || net.getNode(curr).getParents() == null){// ded end
                    return false;
                }
                if(net.getNode(curr).getChildrens() != null){
                    for(String kid: net.getNode(curr).getChildrens()){
                        if(bayesBallRec(net, sorce, dest, kid, true)){
                            return true;
                        }
                    }
                }
                if(net.getNode(curr).getParents() != null){ // if no parents so it is ded end
                    for(String parent: net.getNode(curr).getParents()){// for all the parents if we got the dest so they dependent
                        if(bayesBallRec(net, sorce, dest, parent, false)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }



}