package lib;
import javax.swing.*;

import lib.panel.EntryPanel;
import lib.panel.plot.PlotPanel;

import java.awt.*;

public class GraphingCalculator extends JFrame {

    private PlotPanel plotPanel;
    private EntryPanel functionPanel;
    
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
        plotPanel = new PlotPanel();
        functionPanel = new EntryPanel(plotPanel);
    }
    
    /**
     * Layout the components in the frame
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        add(functionPanel, BorderLayout.WEST);
        add(plotPanel, BorderLayout.CENTER);
    }
}