package com.franosch.bwinf.muellabfuhr;


import lombok.SneakyThrows;

import java.io.File;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Bitte spezifiziere die Nummer der Aufgabe / der Resource");
            System.exit(-1);
        }
        String path = "";
        if (args.length > 1) {
            System.out.println("Nutze Testresourcen");
            path = "muellabfuhr/src/test/resources/";
        } else {
            String pre = getCurrentPath();
            System.out.println(pre);
            path = pre + "resources/";
        }
        int nr = Integer.parseInt(args[0]);
        WeeklyScheduleGenerator weeklyScheduleGenerator = new WeeklyScheduleGenerator(path);
        weeklyScheduleGenerator.findWeeklySchedule(nr);

    }

    @SneakyThrows
    private static String getCurrentPath() {
        String path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();
        String[] splits = path.split("[/\\\\]");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < splits.length - 1; i++) {
            stringBuilder.append(splits[i]).append("/");
        }
        return stringBuilder.toString();
    }

}


