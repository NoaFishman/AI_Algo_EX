import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            nodes.get(key).makeCpt();
        }
    }

    public List<String> allParents(String name){
        ArrayList<String> ans = new ArrayList<>(nodes.get(name).getParents());
        ArrayList<String> ans2 = new ArrayList<>(nodes.get(name).getParents());
        for(String parent: ans){
            ArrayList<String> temp = new ArrayList<>(allParents(parent));
            for(String s: temp){
                if(!ans2.contains(s)){
                    ans2.add(s);
                }
            }
        }
        return ans2;
    }

    public List<String> allKids(String name){
        ArrayList<String> ans = new ArrayList<>(nodes.get(name).getChildrens());
        ArrayList<String> ans2 = new ArrayList<>(nodes.get(name).getChildrens());
        for(String kid: ans){
            ArrayList<String> temp = new ArrayList<>(allParents(kid));
            for(String s: temp){
                if(!ans2.contains(s)){
                    ans2.add(s);
                }
            }
        }
        return ans2;
    }

    public void setKids(){
        for (String key : nodes.keySet()) {
            for(String parent : nodes.get(key).getParents()) {
                nodes.get(parent).addChild(key);
            }
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
