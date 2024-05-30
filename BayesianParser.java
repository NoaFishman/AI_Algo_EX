import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class BayesianParser {

    public static Document parseXML(String Path) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new File(Path));
    }

    public static BayesianNet parse(Document doc) {

        NodeList varNodeList = doc.getElementsByTagName("VARIABLE");
        BayesianNet net = new BayesianNet();

        for (int i = 0; i < varNodeList.getLength(); i++) {
            Element element = (Element) varNodeList.item(i);
            String name = element.getElementsByTagName("NAME").item(0).getTextContent();
            Node currNode = new Node(name);

            NodeList outcomes = element.getElementsByTagName("OUTCOME");
            for (int j = 0; j < outcomes.getLength(); j++) {
                currNode.addOutcome(outcomes.item(j).getTextContent());
            }

            net.addNode(currNode);
        }

        NodeList defnNodesList = doc.getElementsByTagName("DEFINITION");
        for (int i = 0; i < defnNodesList.getLength(); i++) {
            Element element = (Element) defnNodesList.item(i);
            String name = element.getElementsByTagName("FOR").item(0).getTextContent();
            Node currNode = net.getNode(name);

            NodeList givens = element.getElementsByTagName("GIVEN");
            for (int j = 0; j < givens.getLength(); j++) {
                currNode.addParent(givens.item(j).getTextContent());
            }

            String[] probabilities = element.getElementsByTagName("TABLE").item(0).getTextContent().split(" ");
            double[] probs = Arrays.stream(probabilities).mapToDouble(Double::parseDouble).toArray();
            currNode.setProbabilities(probs);
        }

        return net;
    }
}
