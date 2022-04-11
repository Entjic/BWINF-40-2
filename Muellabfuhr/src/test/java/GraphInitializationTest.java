import com.franosch.bwinf.muellabfuhr.Solver;
import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.model.Graph;
import com.franosch.bwinf.muellabfuhr.model.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GraphInitializationTest {
    public final static String TEST_RESOURCES = "src/test/resources/";

    @Test
    public void initTest() {
        FileReader fileReader = new FileReader(0, TEST_RESOURCES);
        Node root = new Node(0);
        Graph graph = new Graph(root);
        graph.initGraph(fileReader);
        Assertions.assertEquals(4, graph.getRoot().getDegree());
        Assertions.assertEquals(2, graph.findById(3).getDegree());
    }

    @Test
    public void findOddDegreeTest0() {
        FileReader fileReader = new FileReader(0, TEST_RESOURCES);
        Solver solver = new Solver();
        solver.initGraph(fileReader);
        int amount = solver.findOddDegree().size();
        Assertions.assertEquals(4, amount);
    }

    @Test
    public void findOddDegreeTest1() {
        FileReader fileReader = new FileReader(1, TEST_RESOURCES);
        Solver solver = new Solver();
        solver.initGraph(fileReader);
        int amount = solver.findOddDegree().size();
        Assertions.assertEquals(4, amount);
    }

}
