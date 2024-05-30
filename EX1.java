import org.w3c.dom.*;
import java.util.*;

public class EX1 {

    public static void main(String[] args) throws Exception {

        System.out.println("please enter the network xml file name");

        Scanner scanner = new Scanner(System.in);
        String path = scanner.next();

        Document doc = BayesianParser.parseXML(path);
        BayesianNet net = BayesianParser.parse(doc);
        System.out.println(net);

    }

//    public static void bayesBall(BayesianNet net, ){

//    }
}