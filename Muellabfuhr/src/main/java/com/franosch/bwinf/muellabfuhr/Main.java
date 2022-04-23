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
        int nr = 0, runner = 5;
        if (args.length == 1) {
            String pre = getCurrentPath();
            System.out.println(pre);
            path = pre + "resources/";
            nr = Integer.parseInt(args[0]);
        }
        if (args.length == 2) {
            String pre = getCurrentPath();
            System.out.println(pre);
            path = pre + "resources/";
            nr = Integer.parseInt(args[0]);
            runner = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            System.out.println("Nutze interne Testresourcen");
            path = "muellabfuhr/src/test/resources/";
            nr = 8;
        }
        WeeklyScheduleGenerator weeklyScheduleGenerator = new WeeklyScheduleGenerator(runner, path);
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


