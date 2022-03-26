package com.franosch.bwinf.muellabfuhr.io;

import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {
    @Getter
    private final List<String> content;

    public FileReader(int fileNumber, String resourceDirectory) {
        content = readFile(fileNumber, resourceDirectory);
    }

    private List<String> readFile(int fileNumber, String resourceDirectory) {
        String current = new File("").getAbsolutePath();
        File file = new File(current + "/" + resourceDirectory + "muellabfuhr" + fileNumber + ".txt");
        List<String> list = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            // Für jede Zeile wiederholen
            while (scanner.hasNextLine()) {
                // Lese einzelne Zeile aus Wortliste
                String data = scanner.nextLine();
                // Füge Zeile zur Liste hinzu
                list.add(data);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Zurückgeben der vollständigen Liste
        return list;
    }

}
