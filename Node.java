import java.util.*;

public class Node {

    private  String name;
    private List<String> parents;
    private List<String> outcomes;
    private double[] table;

    public Node(String name) {
        this.name = name;
        this.parents = new ArrayList<>();
        this.outcomes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getParents() {
        return parents;
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

    public void addOutcome(String outcome) {
        outcomes.add(outcome);
    }

    public void addParent(String parent) {
        parents.add(parent);
    }

    public void setProbabilities(double[] probabilities) {
        table = probabilities;
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", outcomes=" + outcomes +
                ", parents=" + parents +
                ", probabilities=" + Arrays.toString(table) +
                "}\n";
    }
}
