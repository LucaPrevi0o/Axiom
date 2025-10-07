package lib.graph;
import javax.swing.*;

import lib.function.FunctionPanel;

import java.awt.*;

public class GraphingCalculator extends JFrame {

    private GraphPanel graphPanel;
    private FunctionPanel functionPanel;
    
    /**
     * Constructor to set up the GUI components and layout
     */
    public GraphingCalculator() {
        setTitle("Axiom");
        setSize(1000, 600);
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
        functionPanel = new FunctionPanel(graphPanel);
    }
    
    /**
     * Layout the components in the frame
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        add(functionPanel, BorderLayout.WEST);
        add(graphPanel, BorderLayout.CENTER);
    }
}