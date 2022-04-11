import com.franosch.bwinf.muellabfuhr.NextGenSolver;
import com.franosch.bwinf.muellabfuhr.Solver;
import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.model.Node;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SolverTest {

    private FileReader reader;

    @BeforeEach
    void setUp() {
        reader = new FileReader(0, GraphInitializationTest.TEST_RESOURCES);
    }

    @Test
    public void playGround() {
        Solver solver = new Solver();
        solver.initGraph(reader);
        System.out.println(solver.findOddDegree().size());
        solver.makeEven();

    }

    @Test
    public void morePlayGround() {
        NextGenSolver nextGenSolver = new NextGenSolver(reader);
        Graph<Node, DefaultWeightedEdge> graph = nextGenSolver.makeEuler();
        GraphPath<Node, DefaultWeightedEdge> path = nextGenSolver.getEulerCycle(graph);
        System.out.println(path.getStartVertex());
        System.out.println(path.getVertexList());
        System.out.println(path.getWeight());

    }

}
