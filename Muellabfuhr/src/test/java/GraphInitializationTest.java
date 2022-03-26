import com.franosch.bwinf.muellabfuhr.Solver;
import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.model.Graph;
import org.junit.Assert;
import org.junit.Test;

public class GraphInitializationTest {
    public final static String TEST_RESOURCES = "src/test/resources/";

    @Test
    public void initTest(){
        FileReader fileReader = new FileReader(0, TEST_RESOURCES);
        Graph graph = new Graph();
        graph.initGraph(fileReader);
        Assert.assertEquals(4, graph.getRoot().getDegree());
        Assert.assertEquals(2, graph.findById(3).getDegree());
    }

    @Test
    public void findOddDegreeTest0(){
        FileReader fileReader = new FileReader(0, TEST_RESOURCES);
        Solver solver = new Solver();
        solver.initGraph(fileReader);
        int amount = solver.findOddDegree().size();
        Assert.assertEquals(4, amount);
    }

    @Test
    public void findOddDegreeTest1(){
        FileReader fileReader = new FileReader(1, TEST_RESOURCES);
        Solver solver = new Solver();
        solver.initGraph(fileReader);
        int amount = solver.findOddDegree().size();
        Assert.assertEquals(4, amount);
    }

}
