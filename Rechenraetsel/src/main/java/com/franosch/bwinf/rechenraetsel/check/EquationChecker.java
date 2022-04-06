package com.franosch.bwinf.rechenraetsel.check;

import com.franosch.bwinf.rechenraetsel.io.FileReader;
import com.franosch.bwinf.rechenraetsel.model.check.Equation;
import com.franosch.bwinf.rechenraetsel.model.check.Expression;
import com.franosch.bwinf.rechenraetsel.model.check.Variable;
import com.franosch.bwinf.rechenraetsel.model.operation.Operation;
import com.franosch.bwinf.rechenraetsel.model.operation.Simplification;

import java.util.*;

public class EquationChecker {
    private final Map<Integer, List<Equation>> equationMap;

    public EquationChecker() {
        final FileReader fileReader = new FileReader("equations", "/rechenraetsel/src/main/resources/");
        List<String> equationStrings = fileReader.getContent();
        equationMap = new HashMap<>();
        initEquations(equationStrings);
        // System.out.println(equations);
    }

    private void initEquations(List<String> equationStrings) {
        for (String equationString : equationStrings) {
            String left = equationString.split("[=]")[0];
            String right = equationString.split("[=]")[1];
            Equation equation = new Equation(parseExpression(left), parseExpression(right), left + "=" + right);
            put(equation);
        }
    }

    private void put(Equation equation) {
        int length = equation.left().variables().length;
        List<Equation> equations = equationMap.computeIfAbsent(length, k -> new ArrayList<>());
        equations.add(equation);
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

    public boolean satisfiesEquation(Simplification... simplifications) {
        int[] integers = Arrays.stream(simplifications).mapToInt(value -> (int) value.value()).toArray();
        return satisfiesEquation(integers);
    }


    public boolean satisfiesEquation(int[] ints) {
        if (!equationMap.containsKey(ints.length)) return false;
        for (Equation equation : equationMap.get(ints.length)) {
            if (equation.satisfies(ints)) {
                // System.out.println(equation);
                return true;
            }
        }
        return false;
    }

    public boolean satisfiesEquation(int[] ints, int result) {
        if (!equationMap.containsKey(ints.length)) return false;
        for (Equation equation : equationMap.get(ints.length)) {
            if (equation.satisfies(ints, result)) {
                // System.out.println(equation);
                return true;
            }
        }
        return false;
    }


}
