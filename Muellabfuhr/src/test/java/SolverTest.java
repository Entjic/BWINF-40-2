import com.franosch.bwinf.muellabfuhr.NextGenSolver;
import com.franosch.bwinf.muellabfuhr.Solver;
import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.model.Node;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedPerfectMatching;
import org.jgrapht.alg.matching.blossom.v5.ObjectiveSense;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

public class SolverTest {

    @Test
    public void playGround(){
        FileReader fileReader = new FileReader(2, GraphInitializationTest.TEST_RESOURCES);
        Solver solver = new Solver();
        solver.initGraph(fileReader);
        System.out.println(solver.findOddDegree().size());
        solver.makeEven();

    }

    @Test
    public void morePlayGround(){
        FileReader fileReader = new FileReader(8, GraphInitializationTest.TEST_RESOURCES);
        NextGenSolver nextGenSolver = new NextGenSolver(fileReader);
        System.out.println(nextGenSolver.getGraph());
        Graph<Node, DefaultWeightedEdge> graph = nextGenSolver.makeEuler();
        GraphPath<Node, DefaultWeightedEdge> path = nextGenSolver.getEulerCycle(graph);
        System.out.println(path.getStartVertex());
        System.out.println(path.getEndVertex());
        System.out.println(path.getVertexList());
        System.out.println(path.getWeight());

    }

}
