import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphingCalculator calculator = new GraphingCalculator();
            calculator.setVisible(true);
        });
    }
}