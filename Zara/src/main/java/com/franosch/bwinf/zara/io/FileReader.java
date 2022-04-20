package com.franosch.bwinf.zara.io;


import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {
    @Getter
    private final List<String> content;

    public FileReader(String fileName, String resourceDirectory) {
        content = readFile(fileName, resourceDirectory);
    }

    private List<String> readFile(String filename, String resourceDirectory) {
        String current = new File("").getAbsolutePath();
        File file = new File(current + "/" + resourceDirectory + filename + ".txt");
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