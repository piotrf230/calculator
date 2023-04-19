package ComplexCalculator;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.StringJoiner;

public class RPNCalculator {
    Queue<Symbol> symbols;

    public RPNCalculator(String equation) {
        symbols = new LinkedList<>();
        Stack<Character> stack = new Stack<>();
        StringBuilder currentNumber = new StringBuilder();
        boolean wasLastDigit = false;

        equation = removeWhitespaces(equation);
        for (int i = 0; i < equation.length(); i++) {
            char c = equation.charAt(i);
            if (Character.isDigit(c) || c == ',' || c == '.' || (c == '-' && !wasLastDigit && Character.isDigit(equation.charAt(i + 1)))) {
                currentNumber.append(c);
                wasLastDigit = true;
            } else {
                if (currentNumber.length() > 0) symbols.add(new Symbol(Double.parseDouble(currentNumber.toString())));
                currentNumber = new StringBuilder();
                wasLastDigit = false;

                char st;
                switch (c) {
                    case '(', '[' -> stack.push(c);
                    case ')' -> {
                        st = stack.pop();
                        while (st != '(') {
                            symbols.add(new Symbol(st));
                            st = stack.pop();
                        }
                    }
                    case ']' -> {
                        st = stack.pop();
                        while (st != '[') {
                            symbols.add(new Symbol(st));
                            st = stack.pop();
                        }
                        symbols.add(new Symbol('|'));
                    }
                    default -> {
                        byte cPrior = priority(c);
                        while (!stack.empty() && cPrior <= priority(stack.peek())) symbols.add(new Symbol(stack.pop()));
                        stack.add(c);
                    }
                }
            }
        }
        if (currentNumber.length() > 0) symbols.add(new Symbol(Double.parseDouble(currentNumber.toString())));
        while (!stack.empty()) symbols.add(new Symbol(stack.pop()));
    }

    public double calculate() throws Exception {
        Stack<Double> nums = new Stack<>();

        Symbol symbol = symbols.poll();
        while (symbol != null) {
            switch (symbol.getSymbolType()) {
                case NUMBER -> nums.add(symbol.getNumber());
                case OPERATOR -> {
                    double b = nums.pop();
                    if (symbol.getOperation() == Symbol.Operation.ABS) {
                        nums.push(Math.abs(b));
                        break;
                    }
                    double a = nums.pop();
                    switch (symbol.getOperation()) {
                        case ADD -> a += b;
                        case SUB -> a -= b;
                        case MUL -> a *= b;
                        case DIV -> a /= b;
                        case MOD -> a %= b;
                        case POW -> a = Math.pow(a, b);
                        case ROOT -> a = Math.pow(b, 1 / a);
                    }
                    nums.push(a);
                }

            }
            symbol = symbols.poll();
        }

        if (nums.size() != 1) throw new Exception("Something went wrong!");
        return nums.pop();
    }

    private String removeWhitespaces(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) result.append(c);
        }
        return result.toString();
    }

    private byte priority(char op) {
        return switch (op) {
            case '[', '(' -> 0;
            case '+', '-', ')', ']' -> 1;
            case '*', '/', '%' -> 2;
            case '^', '√' -> 3;
            default -> throw new IllegalArgumentException(op + " is not a valid operator!");
        };
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (Symbol s : symbols)
            stringJoiner.add(s.toString());
        return stringJoiner.toString();
    }
}
