package com.franosch.bwinf.rechenraetsel.equationcheck;

import com.franosch.bwinf.rechenraetsel.model.check.Equation;
import com.franosch.bwinf.rechenraetsel.model.check.Expression;
import com.franosch.bwinf.rechenraetsel.model.check.Variable;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EquationChecker {
    private final List<Equation> equations;

    public EquationChecker() {
        List<String> equationStrings = readFile("/rechenraetsel/src/main/resources/");
        equations = parseEquations(equationStrings);
        System.out.println(equations);
    }

    private List<Equation> parseEquations(List<String> equationStrings) {
        final List<Equation> output = new ArrayList<>();
        for (String equationString : equationStrings) {
            String left = equationString.split("[=]")[0];
            String right = equationString.split("[=]")[1];
            Equation equation = new Equation(parseExpression(left), parseExpression(right), left + "=" + right);
            output.add(equation);
        }
        return output;
    }

    private Expression parseExpression(String expressionString) {
        char[] chars = expressionString.toCharArray();
        List<Variable> variables = new ArrayList<>();
        Operation latest = Operation.ADDITION;
        for (char c : chars) {
            if (c == '+' || c == '-' || c == '*' || c == ':') {
                latest = Operation.get(c + "");
                continue;
            }
            Variable variable = new Variable(latest, c);
            variables.add(variable);
        }
        return new Expression(variables.toArray(new Variable[0]));
    }

    public boolean satisfiesEquation(Simplification a, Simplification b, Simplification c) {
        for (Equation equation : equations) {
            if (equation.satisfies(a, b, c)) {
                System.out.println(equation);
                return true;
            }
        }
        return false;
    }

    private List<String> readFile(String resourceDirectory) {
        String current = new File("").getAbsolutePath();
        File file = new File(current + "/" + resourceDirectory + "equations" + ".txt");
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
