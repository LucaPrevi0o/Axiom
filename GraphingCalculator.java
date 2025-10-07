import javax.swing.*;
import java.awt.*;

public class GraphingCalculator extends JFrame {

    private GraphPanel graphPanel;
    private JTextField inputField;
    private JButton plotButton;
    
    /**
     * Constructor to set up the GUI components and layout
     */
    public GraphingCalculator() {

        setTitle("Axiom");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        layoutComponents();
    }
    
    /**
     * Initialize GUI components
     */
    private void initComponents() {

        graphPanel = new GraphPanel();
        inputField = new JTextField("x^2");
        plotButton = new JButton("Plot");
        
        plotButton.addActionListener(e -> plotFunction());
    }
    
    /**
     * Layout the components in the frame
     */
    private void layoutComponents() {

        setLayout(new BorderLayout(5, 5));
        
        // Top panel for input
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(new JLabel("f(x) = "), BorderLayout.WEST);
        topPanel.add(inputField, BorderLayout.CENTER);
        topPanel.add(plotButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
    }
    
    /**
     * Plot the function based on user input
     */
    private void plotFunction() {

        String expression = inputField.getText();
        graphPanel.setFunction(expression);
        graphPanel.repaint();
    }
}