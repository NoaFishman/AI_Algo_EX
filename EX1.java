import org.w3c.dom.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class EX1 {

    public static void main(String[] args) throws Exception {
        String line;
        BayesianNet net = new BayesianNet();
        String filename = "input.txt";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            if ((line = br.readLine()) != null) {
                Document doc = BayesianParser.parseXML(line);
                net = BayesianParser.parse(doc);
                net.setParentsOut();
                net.setCpt();
                //System.out.println(net);
            }
            while((line = br.readLine()) != null){
                if(line.charAt(0) == 'P'){
                    // variable elimination
                    String[] s = line.split("\\)");
                    String[] hiddens = s[1].split("-");
                    String[] e = s[0].split("\\|");
                    String[] evidence = e[1].split(",");
                    Map<String, String> evi = new HashMap<>();
                    for (int i = 0; i < evidence.length; i++) {
                        String[] v = evidence[i].split("=");
                        evi.put(v[0], v[1]);
                    }
                    String[] q = e[0].split("\\(");
                    String[] query = q[1].split("=");
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
        if(net.getNode(curr).isColor() && fromParents){ //????????
            return false;
        }
        net.getNode(curr).setColor(true); // coloring this node
        if(net.getNode(dest) == net.getNode(curr)){ // if this is the dest so dependent
            return true;
        }
        if(net.getNode(curr).isEvidence()){// if evidence so go up to the parents
            if(net.getNode(curr).getParents() == null){ // if no parents so it is ded end
                return false;
            }
            for(String parent: net.getNode(curr).getParents()){// for all the parents if we got the dest so they dependent
                if(bayesBallRec(net, sorce, dest, parent, false)){
                    return true;
                }
            }
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

    public static void VariableElimination(List<String> hidden, BayesianNet net, Map<String,String> evidence){
        List<Map<Map<String,String>,Double>> factoris = new ArrayList<>();
        for(String key: net.getNodes().keySet()){
            Map<Map<String, String>, Double> fi = new HashMap<>();
            fi = net.getNode(key).getCpt();
            for(Map<String,String> temp : fi.keySet()){
                // another for loop????????
                // feel to many loops here but i don't know
            }
        }




    }



}