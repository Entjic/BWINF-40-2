package com.franosch.bwinf.rechenraetsel.check;

import com.franosch.bwinf.rechenraetsel.io.FileReader;
import com.franosch.bwinf.rechenraetsel.model.check.blacklist.BlacklistEntry;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlacklistChecker {
    private final Map<Integer, List<BlacklistEntry>> entries;

    public BlacklistChecker() {
        final FileReader fileReader = new FileReader("blacklist", "/rechenraetsel/src/main/resources/");
        List<String> inputStrings = fileReader.getContent();
        entries = new HashMap<>();
        parseStrings(inputStrings);
    }

    private void parseStrings(List<String> inputStrings) {
        for (String inputString : inputStrings) {
            BlacklistEntry entry = new BlacklistEntry(inputString);
            put(entry);
        }
    }

    private void put(BlacklistEntry blacklistEntry) {
        int length = blacklistEntry.getLength();
        List<BlacklistEntry> equations = entries.computeIfAbsent(length, k -> new ArrayList<>());
        equations.add(blacklistEntry);
    }

    public boolean matchesBlacklistedEntry(Simplification... simplifications) {
        int length = simplifications.length;
        if (!entries.containsKey(length)) return false;
        for (BlacklistEntry entry : entries.get(length)) {
            if (entry.satisfies(simplifications)) return true;
        }
        return false;
    }


}
