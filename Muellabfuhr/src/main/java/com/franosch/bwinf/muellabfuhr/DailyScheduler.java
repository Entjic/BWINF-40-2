package com.franosch.bwinf.muellabfuhr;

import com.franosch.bwinf.muellabfuhr.io.FileReader;
import com.franosch.bwinf.muellabfuhr.model.Schedule;
import com.franosch.bwinf.muellabfuhr.model.graph.Circle;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class DailyScheduler {
    private final int fileNumber;
    private final String filePath;
    private final int k = 5;

    public Set<Schedule> generateSchedules(){
        Solver solver = new Solver();
        solver.initGraph(new FileReader(fileNumber, filePath));
        solver.makeEven();
        List<Circle> cpp = solver.solveChinesePostmanProblem();
        Map<Integer, List<Circle>> runner = solver.allocate(k, cpp);
        return new HashSet<>();
    }
}
