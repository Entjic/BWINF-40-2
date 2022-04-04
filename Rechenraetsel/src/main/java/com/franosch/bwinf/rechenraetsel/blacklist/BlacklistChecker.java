package com.franosch.bwinf.rechenraetsel.blacklist;

import com.franosch.bwinf.rechenraetsel.io.FileReader;
import com.franosch.bwinf.rechenraetsel.model.check.blacklist.BlacklistEntry;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlacklistChecker {
    private final Set<BlacklistEntry> entries;

    public BlacklistChecker() {
        final FileReader fileReader = new FileReader("blacklist", "/rechenraetsel/src/main/resources/");
        List<String> inputStrings = fileReader.getContent();
        entries = parseStrings(inputStrings);
    }

    private Set<BlacklistEntry> parseStrings(List<String> inputStrings) {
        final Set<BlacklistEntry> set = new HashSet<>();
        for (String inputString : inputStrings) {
            BlacklistEntry entry = new BlacklistEntry(inputString);
            set.add(entry);
        }
        return set;
    }

    public boolean matchesBlacklistedEntry(Simplification... simplifications) {
        for (BlacklistEntry entry : entries) {
            if (entry.satisfies(simplifications)) return true;
        }
        return false;
    }


}
