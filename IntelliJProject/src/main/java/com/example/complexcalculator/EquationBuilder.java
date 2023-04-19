package com.example.complexcalculator;

import java.util.Stack;

public class EquationBuilder {
    private enum CharType {
        DIGIT, OPERATOR, BRACKET_OPEN, BRACKET_CLOSE, ABS_OPEN, ABS_CLOSE, INIT
    }

    private enum BracketType {
        NORM, ABS
    }

    private StringBuilder string;

    private Stack<BracketType> openBrackets;
    private boolean finished;
    private double result;
    private final char decimalDelimiter;
    private boolean isCurrNumInDec;

    private CircularBuffer<String> history;
    private CircularBuffer<CharType> lastTyped;
    private int move;

    public EquationBuilder(int bufferCapacity) {
        string = new StringBuilder();
        openBrackets = new Stack<>();
        finished = false;
        move = 0;

        history = new CircularBuffer<>(bufferCapacity);
        lastTyped = new CircularBuffer<>(bufferCapacity);
        decimalDelimiter = Double.valueOf(0).toString().charAt(1);

        history.add("");
        lastTyped.add(CharType.INIT);
    }

    public EquationBuilder() {
        this(10);
    }

    public void append(char c) {
        if (finished) {
            string = new StringBuilder();
            openBrackets = new Stack<>();
            finished = false;
            move = 0;
            history = new CircularBuffer<>(history.getCapacity());
            lastTyped = new CircularBuffer<>(lastTyped.getCapacity());

            if (!Character.isDigit(c))
                for (char d : cutDecimalZeros(Double.valueOf(result).toString()).toCharArray()) append(d);
        }

        boolean isDecimalDelimiter = c == decimalDelimiter;
        if (isDecimalDelimiter && isCurrNumInDec) return;
        if (Character.isDigit(c) || isDecimalDelimiter) {
            string.append(c);
            lastTyped.add(CharType.DIGIT);
            history.add(string.toString());
            isCurrNumInDec = isDecimalDelimiter || isCurrNumInDec;
            move = Math.min(++move, history.getCapacity());
            return;
        }
        switch (c) {
            case '(' -> {
                openBracket(c);
                openBrackets.push(BracketType.NORM);
                lastTyped.add(CharType.BRACKET_OPEN);
            }
            case '[' -> {
                openBracket(c);
                openBrackets.push(BracketType.ABS);
                lastTyped.add(CharType.ABS_OPEN);
            }
            case ')' -> {
                if (openBrackets.peek() == BracketType.NORM) {
                    if (lastTyped.peek() == CharType.OPERATOR) string.setCharAt(string.length() - 1, c);
                    else string.append(c);
                    openBrackets.pop();
                } else return;
            }
            case ']' -> {
                if (openBrackets.peek() == BracketType.ABS) {
                    if (lastTyped.peek() == CharType.OPERATOR) string.setCharAt(string.length() - 1, c);
                    else string.append(c);
                    openBrackets.pop();
                } else return;
            }
            default -> { // must be an operator here
                CharType last = lastTyped.peek();
                if (last == CharType.DIGIT) {
                    string.append(c);
                    lastTyped.add(CharType.OPERATOR);
                } else if (last == CharType.OPERATOR) string.setCharAt(string.length() - 1, c);
                else if ((last == CharType.ABS_OPEN || last == CharType.BRACKET_OPEN || string.isEmpty()) && c == '-')
                    string.append(c);
                else if ((last == CharType.ABS_OPEN || last == CharType.BRACKET_OPEN) && lastTyped.peek(1) == CharType.DIGIT)
                    string.append(c);
                else return;
            }
        }
        history.add(string.toString());
        move = Math.min(++move, history.getCapacity());
    }

    private void openBracket(char bracket) {
        if (lastTyped.peek() == CharType.DIGIT) {
            int i = string.length() - 1;
            while (i > 0 && Character.isDigit(string.charAt(i - 1))) i--;
            string.insert(i, bracket);
        } else {
            string.append(bracket);
        }
    }

    public void undo() {
        if (move == 0) return;
        move--;
        string = new StringBuilder(history.back());
        CharType last = lastTyped.back();
        finished = false;
        switch (last) {
            case ABS_OPEN, BRACKET_OPEN -> openBrackets.pop();
            case BRACKET_CLOSE -> openBrackets.push(BracketType.NORM);
            case ABS_CLOSE -> openBrackets.push(BracketType.ABS);
        }
    }

    public void finish(double result) {
        string.append("=").append(cutDecimalZeros(Double.valueOf(result).toString()));
        finished = true;
        this.result = result;
    }

    public void closeBrackets() {
        while (!openBrackets.isEmpty()) {
            BracketType type = openBrackets.peek();
            switch (type) {
                case NORM -> append(')');
                case ABS -> append(']');
            }
        }
    }

    @Override
    public String toString() {
        return string.toString();
    }

    public String toString(boolean withSpaces) {
        if (!withSpaces) return toString();
        String spacedOperators = "+-*/%=";
        StringBuilder result = new StringBuilder(string);
        for (int i = 0; i < result.length(); i++) {
            char c = result.charAt(i);
            char next = i < result.length() - 1 ? result.charAt(i + 1) : '0';
            char prev = i > 0 ? result.charAt(i - 1) : 0;
            if (!(c == '-' && Character.isDigit(next) && !Character.isDigit(prev)) && spacedOperators.indexOf(c) >= 0) {
                result.insert(i + 1, ' ');
                result.insert(i, ' ');
                i += 2;
            }
        }
        return result.toString();
    }

    private String cutDecimalZeros(String s) {
        if (s.indexOf(decimalDelimiter) < 0) return s;

        StringBuilder sb = new StringBuilder(s);
        int i = sb.length() - 1;
        while (sb.charAt(i) == '0') i--;
        if (sb.charAt(i) != decimalDelimiter) i++;
        sb.delete(i, sb.length());

        return sb.toString();
    }
}
