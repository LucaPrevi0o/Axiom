package lib.graph;
import javax.swing.*;

import lib.function.FunctionPanel;

import java.awt.*;

public class GraphingCalculator extends JFrame {

    private GraphPanel graphPanel;
    private FunctionPanel functionPanel;
    private JSplitPane splitPane;
    
    // Collapse state tracking
    private boolean isCollapsed = false;
    private int lastDividerLocation = 280;
    private static final int COLLAPSED_SIZE = 12;
    private static final int MIN_EXPANDED_SIZE = 250;
    private static final int MAX_EXPANDED_SIZE = 450;
    
    /**
     * Constructor to set up the GUI components and layout
     */
    public GraphingCalculator() {
        setTitle("Axiom");
        setSize(1200, 800);
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
        // Create a split pane with resizable divider
        splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            functionPanel,
            graphPanel
        );
        
        // Set initial divider location (280 pixels for function panel)
        splitPane.setDividerLocation(280);
        
        // Set minimum sizes
        functionPanel.setMinimumSize(new Dimension(0, 0)); // Allow full collapse
        graphPanel.setMinimumSize(new Dimension(400, 0));
        
        // Add property change listener to enforce size constraints and track divider position
        splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, evt -> {
            if (!isCollapsed) {
                int location = splitPane.getDividerLocation();
                
                // Enforce min/max constraints when expanded
                if (location < MIN_EXPANDED_SIZE) {
                    SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(MIN_EXPANDED_SIZE));
                    lastDividerLocation = MIN_EXPANDED_SIZE;
                } else if (location > MAX_EXPANDED_SIZE) {
                    SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(MAX_EXPANDED_SIZE));
                    lastDividerLocation = MAX_EXPANDED_SIZE;
                } else {
                    lastDividerLocation = location;
                }
            }
        });
        
        // Add mouse listener to the divider for double-click
        addDividerDoubleClickListener();
        
        // Add split pane to frame
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }
    
    /**
     * Add double-click listener to the split pane divider
     */
    private void addDividerDoubleClickListener() {
        // Access the divider component
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < splitPane.getComponentCount(); i++) {
                Component comp = splitPane.getComponent(i);
                if (comp.getClass().getName().contains("Divider")) {
                    comp.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            if (e.getClickCount() == 2) {
                                toggleCollapse();
                            }
                        }
                    });
                    break;
                }
            }
        });
    }
    
    /**
     * Toggle between collapsed and expanded states
     */
    private void toggleCollapse() {
        if (isCollapsed) {
            // Expand to last known position (or default)
            int expandLocation = lastDividerLocation;
            // Ensure within bounds
            if (expandLocation < MIN_EXPANDED_SIZE) {
                expandLocation = MIN_EXPANDED_SIZE;
            } else if (expandLocation > MAX_EXPANDED_SIZE) {
                expandLocation = MAX_EXPANDED_SIZE;
            }
            splitPane.setDividerLocation(expandLocation);
            functionPanel.setVisible(true);
            isCollapsed = false;
        } else {
            // Save current location if it's valid
            int currentLocation = splitPane.getDividerLocation();
            if (currentLocation >= MIN_EXPANDED_SIZE && currentLocation <= MAX_EXPANDED_SIZE) {
                lastDividerLocation = currentLocation;
            }
            // Collapse to very small size
            splitPane.setDividerLocation(COLLAPSED_SIZE);
            functionPanel.setVisible(false);
            isCollapsed = true;
        }
    }
}