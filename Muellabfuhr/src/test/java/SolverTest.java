import com.franosch.bwinf.muellabfuhr.Solver;
import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.model.Result;
import com.franosch.bwinf.muellabfuhr.model.graph.Cycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class SolverTest {

    private FileReader reader;

    @BeforeEach
    void setUp() {
        reader = new FileReader(7, GraphInitializationTest.TEST_RESOURCES);
    }

    @Test
    public void playGround() {
        Solver solver = new Solver();
        solver.initGraph(reader);
        System.out.println(solver.findOddDegree().size());
        solver.makeEven();
        List<Cycle> cpp = solver.solveChinesePostmanProblem();
        List<Result> results = solver.allocate(5, cpp);
        Collections.sort(results);
        System.out.println(results);

    }



}
