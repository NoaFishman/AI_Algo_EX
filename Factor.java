import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class Factor implements Comparable<Factor>{
    Map<Map<String, String>, Double> cpt = new HashMap<>();
    ArrayList<String> varible = new ArrayList<>();
    static int add = 0;
    static int multi =0;

    public Factor(){
        this.cpt = new HashMap<>();
        this.varible = new ArrayList<>();
    }

    public Factor( Map<Map<String, String>, Double> cptCurr){
        for(Map<String, String> key: cptCurr.keySet()){
            Map<String, String> temp = new HashMap<>();
            for(String s: key.keySet()){
                temp.put(s, key.get(s));
            }
            if(varible.isEmpty()){
                varible.addAll(key.keySet());
            }
            this.cpt.put(temp, cptCurr.get(key));
        }


    }

    public Factor(Factor other){
        for(Map<String, String> key: other.cpt.keySet()){
            Map<String, String> temp = new HashMap<>();
            for(String s: key.keySet()){
                temp.put(s, key.get(s));
            }
            this.cpt.put(temp, other.cpt.get(key));
        }
        this.varible.addAll(other.varible);
    }

    public int asciiSize(){
        int ans=0;
        for(int i=0; i<varible.size(); i++){
            for(int j=0; j<varible.get(i).length(); j++){
                ans += varible.get(i).charAt(j);
            }
        }
        return ans;
    }

    @Override
    public int compareTo(Factor other) {
        if(this.varible.size() > other.varible.size()){
            return 1;
        }
        else if(this.varible.size() < other.varible.size()){
            return -1;
        }
        else{
            if(this.asciiSize() > other.asciiSize()){
                return 1;
            }
            else if(this.asciiSize() < other.asciiSize()){
                return  -1;
            }
        }
        return 0;
    }

    public void removeIrrelevant(ArrayList<String> evi){
        ArrayList<String> evidence = new ArrayList<>();
        for(int i=0; i< evi.size(); i++){
            String[] e = evi.get(i).split("=");
            evidence.addAll(Arrays.asList(e));
        }

        ArrayList<Map<String,String>> toRemove = new ArrayList<>();
        for(Map<String,String> key: cpt.keySet()){
            for(String s: key.keySet()){
                for(int j=0; j<evidence.size()-1; j+=2){
                    if(evidence.get(j).equals(s) && !evidence.get(j+1).equals(key.get(s))){
                        toRemove.add(key);
                    }
                }
            }
        }
        for (Map<String, String> stringStringMap : toRemove) {
            cpt.remove(stringStringMap);
        }
    }

    public void removeEvi(ArrayList<String> evi){
        for(String e: evi){
            e = e.split("=")[0];
            ArrayList<Map<String, String>> toRemove = new ArrayList<>();
            for(Map<String, String> key: this.cpt.keySet()){
                if(key.containsKey(e)){
                    toRemove.add(key);
                }
            }
            for(int i=0; i<toRemove.size(); i++){
                Double x = this.cpt.get(toRemove.get(i));
                this.cpt.remove(toRemove.get(i));
                toRemove.get(i).remove(e);
                this.cpt.put(toRemove.get(i), x);
            }
            this.varible.remove(e);
        }
    }

    public boolean containsVar(String var){
        return varible.contains(var);
    }

    public Factor join(Factor f){

        Map<Map<String, String>, Double> currCpt = new HashMap<>();
        ArrayList<String> sharing =this.getSharingVarible(f);


        for(Map<String, String> key1: this.cpt.keySet()){
            for(Map<String, String> key2: f.cpt.keySet()){
                boolean flag = true;
                for(String var: sharing){
                    if(!key1.get(var).equals(key2.get(var))){
                        flag = false;
                    }
                }
                if(flag){
                    Map<String, String> keyCurr = new HashMap<>();
                    for(String s: key1.keySet()){
                        keyCurr.put(s, key1.get(s));
                    }
                    for(String s: key2.keySet()){
                        if(!sharing.contains(s)){
                            keyCurr.put(s, key2.get(s));
                        }
                    }
                    currCpt.put(keyCurr, f.cpt.get(key2)*this.cpt.get(key1));
                    multi++;
                }
            }
        }
        return new Factor(currCpt);
    }

    public void eliminate(String hiddden){

        List<Map<String, String>> keys = new ArrayList<>(this.cpt.keySet().stream().toList());
        List<Integer> index = new ArrayList<>();
        for(int i=0; i<keys.size(); i++){
            for(int j=0; j<keys.size(); j++){
                if(j!=i && !index.contains(j)){
                    boolean flag = true;
                    for(String s: keys.get(i).keySet()){
                        if(!s.equals(hiddden)){
                            if(!keys.get(i).get(s).equals(keys.get(j).get(s))){
                                flag = false;
                            }
                        }
                        else{
                            if(keys.get(i).get(s).equals(keys.get(j).get(s))){
                                flag = false;
                            }
                        }
                    }
                    if(flag){
                        index.add(i);
                        index.add(j);
                        Map<String, String> newKey =new HashMap<>();
                        for(String s:keys.get(i).keySet()){
                            if(!s.equals(hiddden)){
                                newKey.put(s, keys.get(i).get(s));
                            }
                        }
                        Double x = this.cpt.get(keys.get(i)) + this.cpt.get(keys.get(j));
                        this.cpt.put(newKey, x);
                        add++;
                    }
                }
            }
        }
        for (Map<String, String> key : keys) {
            this.cpt.remove(key);
        }
        this.varible.remove(hiddden);
    }

    public ArrayList<String> getSharingVarible(Factor other) {
        ArrayList<String> sharing = new ArrayList<>();
        for(String var: this.varible){
            if(other.containsVar(var)){
                sharing.add(var);
            }
        }
        return sharing;
    }

    public String toString(){
        StringBuilder ans = new StringBuilder("variables: " + varible + "\n");
        for(Map<String, String> key: this.cpt.keySet()){
            ans.append(key);
            ans.append(this.cpt.get(key));
            ans.append("\n");
        }
        return ans.toString();
    }

    public void normalize(){
        Double sum = 0.0;
        for(Map<String, String> key: this.cpt.keySet()){
            sum += this.cpt.get(key);
            add++;
        }
        for(Map<String, String> key: this.cpt.keySet()){
            Double x = this.cpt.get(key) / sum;
            this.cpt.replace(key, this.cpt.get(key), x);
        }
        add--;
    }

    public String getValue(String query){
        String[] q = query.split("=");
        for(Map<String, String> key: cpt.keySet()){
            for(String s: key.keySet()){
                if(key.get(s).equals(q[1]) && s.equals(q[0])){
                    NumberFormat formatter = new DecimalFormat("#0.00000");
                    return formatter.format(this.cpt.get(key));
                }
            }
        }
        return "";
    }

    public int getAdd(){
        return add;
    }

    public int getMulti(){
        return multi;
    }

    public void setAddMulti(){
        add =0;
        multi =0;
    }
}








