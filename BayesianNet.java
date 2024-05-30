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

    public Node getNode(String name) {
        return nodes.get(name);
    }

    @Override
    public String toString() {
        return "BayesianNetwork{" +
                "nodes=\n" + nodes.values() +
                '}';
    }
}
