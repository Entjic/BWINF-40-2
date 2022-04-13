package com.franosch.bwinf.muellabfuhr;

import com.franosch.bwinf.muellabfuhr.model.Schedule;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class DailyScheduler {
    private final int fileNumber;


    public Set<Schedule> generateSchedules(){
        return new HashSet<>();
    }
}
