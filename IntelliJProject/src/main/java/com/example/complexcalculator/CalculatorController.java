package com.example.complexcalculator;

import ComplexCalculator.RPNCalculator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CalculatorController {
    private EquationBuilder eq;

    @FXML
    private Label Equation;
    @FXML
    private Button Decimal;

    public void setup() {
        eq = new EquationBuilder(20);
        Decimal.setText(Double.valueOf(0).toString().charAt(1) + "");
    }

    @FXML
    private void onPress(ActionEvent event) {
        char pressed = ((Button) event.getSource()).getText().charAt(0);
        switch (pressed) {
            case '<' -> eq.undo();
            case '=' -> {
                eq.closeBrackets();
                RPNCalculator calc = new RPNCalculator(eq.toString());
                try {
                    eq.finish(calc.calculate());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            default -> eq.append(pressed);
        }
        refresh();
    }

    public void refresh(){
        Equation.setText(eq.toString(true));
    }
}