import javax.swing.*;

import lib.ui.GraphingCalculator;

public class Axiom {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphingCalculator calculator = new GraphingCalculator();
            calculator.setVisible(true);
        });
    }
}