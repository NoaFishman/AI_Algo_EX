import java.util.*;

public class Node {

    private String name;
    private List<String> parents;
    private List<String> outcomes;
    private List<String> childrens;
    private double[] table;
    private Map<String, List<String>> parentOut = new HashMap<>();
    private Map<Map<String,String>,Double> cpt = new HashMap<>();
    boolean evidence;
    boolean color;

    public Node(String name) {
        this.name = name;
        this.parents = new ArrayList<>();
        this.outcomes = new ArrayList<>();
        this.childrens = new ArrayList<>();
        evidence = false;
        color = false;
    }

    public String getName() {
        return name;
    }

    public List<String> getParents() {
        return parents;
    }

    public List<String> getChildrens(){
        return childrens;
    }

    public boolean isEvidence() {
        return evidence;
    }

    public boolean isColor() {
        return color;
    }

    public Map<Map<String, String>,Double> getCpt(){
        return cpt;
    }

    public List<String> getOutcomes() {
        return outcomes;
    }

    public double[] getTable() {
        return table;
    }

    public void setTable(double[] table) {
        this.table = table;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public void addOutcome(String outcome) {
        outcomes.add(outcome);
    }

    public void addParent(String parent) {
        parents.add(parent);
    }

    public void setEvidence(boolean evidence) {
        this.evidence = evidence;
    }

    public void setProbabilities(double[] probabilities) {
        table = probabilities;
    }

    public void addChild(String child) {
        childrens.add(child);
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", outcomes=" + outcomes +
                ", parents=" + parents +
                ", childrens=" + childrens +
                ", probabilities=" + Arrays.toString(table) +
                ", cpt=" + cpt +
                "}\n";
    }

    public void makeCpt(){
        parentOut.put(name, outcomes);
        parents.add(name);
        makeCptRec(new HashMap<>(), parents, 0, 0);
        parentOut.remove(name);
        parents.remove(name);
    }

    public void makeCptRec(Map<String,String> parentValues, List<String> parentsNames, int parentIndex, int tableIndex){
        // there is no more parents to read
        if(parentIndex == parents.size()){
            cpt.put(new HashMap<>(parentValues), table[tableIndex]);
        }
        else{
            // get the current parent name
            String parent = parents.get(parentIndex);
            //get the current parent outcomes list
            List<String> pOutcomes = parentOut.get(parent);

            for(String outcome : pOutcomes){
                parentValues.put(parent, outcome);

                // calculate the current index in the values table
                int outcomeIndex = pOutcomes.indexOf(outcome);
                int newIndex = tableIndex * pOutcomes.size() + outcomeIndex;

                makeCptRec(parentValues, parentsNames, parentIndex+1, newIndex);
            }
            parentValues.remove(parent);
        }
    }

    public void setParentsOut(String pName, List<String> pOutcomes){
        parentOut.put(pName,pOutcomes);
    }


}
