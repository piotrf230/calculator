package ComplexCalculator;

public class Symbol {
    public enum Type {
        NUMBER, OPERATOR
    }

    public enum Operation {
        ADD, SUB, MUL, DIV, MOD, POW, ROOT, ABS
    }

    private Type type;
    private double num;
    private Operation op;

    public Symbol(double num) {
        type = Type.NUMBER;
        this.num = num;
    }

    public Symbol(Operation operation) {
        type = Type.OPERATOR;
        op = operation;
    }

    public Symbol(char operator) {
        type = Type.OPERATOR;
        switch (operator) {
            case '+' -> op = Operation.ADD;
            case '-' -> op = Operation.SUB;
            case '*' -> op = Operation.MUL;
            case '/' -> op = Operation.DIV;
            case '%' -> op = Operation.MOD;
            case '^' -> op = Operation.POW;
            case '√' -> op = Operation.ROOT;
            case '|' -> op = Operation.ABS;
            default -> throw new IllegalArgumentException(operator + " is not a valid operator!");
        }
    }

    public Type getSymbolType() {
        return type;
    }

    public double getNumber() throws Exception {
        if (type != Type.NUMBER) throw new Exception("Symbol is " + type + ", not a number!");
        return num;
    }

    public Operation getOperation() throws Exception {
        if (type != Type.OPERATOR) throw new Exception("Symbol is " + type + ", not an operation");
        return op;
    }

    @Override
    public String toString() {
        switch (type) {
            case NUMBER -> {
                return "" + num;
            }
            case OPERATOR -> {
                return "" + op;
            }
            default -> {
                return "UNKNOWN SYMBOL";
            }
        }
    }
}
