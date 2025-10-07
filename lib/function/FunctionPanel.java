package lib.function;

import lib.graph.GraphPanel;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for managing function entries
 * Now uses FunctionColorManager for color management
 */
public class FunctionPanel extends JPanel {
    
    private final GraphPanel graphPanel;
    private final List<FunctionEntry> functionEntries;
    private final FunctionColorManager colorManager;
    private final JPanel entriesPanel;
    private final JButton addButton;
    
    private java.util.Map<String, String> namedFunctions = new java.util.HashMap<>();
    
    /**
     * Constructor
     * @param graphPanel Graph panel to update
     */
    public FunctionPanel(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.functionEntries = new ArrayList<>();
        this.colorManager = new FunctionColorManager();
        this.entriesPanel = new JPanel();
        this.addButton = new JButton("+ Add Function");
        
        initComponents();
        layoutComponents();
        
        // Add default function
        addFunction("f(x)=x^2");
    }
    
    /**
     * Initialize components
     */
    private void initComponents() {
        // Set preferred width for initial split pane layout (height will be determined by parent)
        setPreferredSize(new Dimension(280, 600));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));
        
        addButton.addActionListener(e -> addFunction(""));
    }
    
    /**
     * Layout components
     */
    private void layoutComponents() {
        setLayout(new BorderLayout(0, 10));
        
        JLabel titleLabel = new JLabel("Functions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(entriesPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);
        
        add(addButton, BorderLayout.SOUTH);
    }
    
    /**
     * Add a new function
     */
    public void addFunction(String expression) {
        Color color = colorManager.getNextColor();
        FunctionEntry entry = new FunctionEntry(expression, color, this);
        
        functionEntries.add(entry);
        entriesPanel.add(entry);
        entriesPanel.add(Box.createVerticalStrut(5));
        
        updateGraph();
        revalidate();
        repaint();
    }
    
    /**
     * Remove a function
     */
    public void removeFunction(FunctionEntry entry) {
        functionEntries.remove(entry);
        entriesPanel.remove(entry);
        
        // Remove spacing after this entry
        Component[] components = entriesPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == entry && i + 1 < components.length) {
                entriesPanel.remove(i + 1);
                break;
            }
        }
        
        updateGraph();
        revalidate();
        repaint();
    }
    
    /**
     * Update graph with all current functions
     */
    public void updateGraph() {
        FunctionParser.ParseResult result = FunctionParser.parseEntries(functionEntries);
        
        namedFunctions = result.getNamedFunctions();
        graphPanel.setUserFunctions(namedFunctions);
        graphPanel.setFunctions(result.getGraphFunctions());
        graphPanel.repaint();
    }
    
    public java.util.Map<String, String> getNamedFunctions() {
        return namedFunctions;
    }
}