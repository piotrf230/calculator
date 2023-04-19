package RPNTest;

import ComplexCalculator.RPNCalculator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws Exception {
        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            RPNCalculator calculator = new RPNCalculator(reader.readLine());
            System.out.println(calculator.toString());
            System.out.println(calculator.calculate());
        }
    }
}
