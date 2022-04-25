package com.franosch.bwinf.muellabfuhr;


import lombok.SneakyThrows;

import java.io.File;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Bitte spezifiziere die Nummer der Aufgabe / der Resource");
            System.out.println("Aufgabe 0 mit 5 Runnern: ");
            System.out.println("java -jar Muellabfuhr.jar 0");
            System.out.println("Aufgabe 5 mit 4 Runnern: ");
            System.out.println("Beispiel java -jar Muellabfuhr.jar 5 4");
            System.exit(-1);
        }
        String path;
        int nr, runner = 5;
        if (args.length == 1) {
            String pre = getCurrentPath();
            System.out.println(pre);
            path = pre + "resources/";
            nr = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            String pre = getCurrentPath();
            System.out.println(pre);
            path = pre + "resources/";
            nr = Integer.parseInt(args[0]);
            runner = Integer.parseInt(args[1]);
            if (runner == 0) throw new UnsupportedOperationException("At least 1 Runner required");
        } else {
            System.out.println("Nutze interne Testresourcen");
            path = new File("").getAbsolutePath() + "/muellabfuhr/src/test/resources/";
            nr = 7;
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


