import java.util.HashMap;
import java.util.Map;

public class BayesianNet {

    private Map<String, Node> nodes;

    public BayesianNet() {
        nodes = new HashMap<>();
    }

    public void addNode(Node node) {
        nodes.put(node.getName(), node);
    }

    // for each node add to his parents outcomes map all the parents and their outcomes list
    public void setParentsOut(){
        for (String key : nodes.keySet()) {
            for(String parent : nodes.get(key).getParents()) {
                nodes.get(key).setParentsOut(parent, nodes.get(parent).getOutcomes());
            }
        }
    }

    public void setCpt(){
        for (String key : nodes.keySet()) {
            for(String parent : nodes.get(key).getParents()) {
                nodes.get(parent).addChild(key);
            }
        }
    }

    public void setKids(){
        for (String key : nodes.keySet()) {

        }
    }

    public Node getNode(String name) {
        return nodes.get(name);
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return "BayesianNetwork{" +
                "nodes=\n" + nodes.values() +
                '}';
    }
}
