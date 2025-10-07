import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FunctionPanel extends JPanel {
    
    private GraphPanel graphPanel;
    private List<FunctionEntry> functionEntries;
    private JPanel entriesPanel;
    private JButton addButton;
    
    private static final Color[] FUNCTION_COLORS = {
        Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, 
        Color.MAGENTA, Color.CYAN, new Color(139, 69, 19),
        new Color(128, 0, 128)
    };
    
    /**
     * Constructor to set up the function panel
     * @param graphPanel The graph panel to update when functions change
     */
    public FunctionPanel(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.functionEntries = new ArrayList<>();
        
        initComponents();
        layoutComponents();
        
        // Add a default function
        addFunction("x^2");
    }
    
    /**
     * Initialize GUI components
     */
    private void initComponents() {
        setPreferredSize(new Dimension(280, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        entriesPanel = new JPanel();
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));
        
        addButton = new JButton("+ Add Function");
        addButton.addActionListener(e -> addFunction(""));
    }
    
    /**
     * Layout the components in the panel
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
     * Add a new function to the list
     * @param expression The initial expression for the function
     */
    public void addFunction(String expression) {
        Color color = FUNCTION_COLORS[functionEntries.size() % FUNCTION_COLORS.length];
        FunctionEntry entry = new FunctionEntry(expression, color, this);
        
        functionEntries.add(entry);
        entriesPanel.add(entry);
        entriesPanel.add(Box.createVerticalStrut(5));
        
        updateGraph();
        revalidate();
        repaint();
    }
    
    /**
     * Remove a function from the list
     * @param entry The function entry to remove
     */
    public void removeFunction(FunctionEntry entry) {
        functionEntries.remove(entry);
        entriesPanel.remove(entry);
        
        // Remove the spacing component after this entry
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
     * Update the graph with all current functions
     */
    public void updateGraph() {
        List<GraphFunction> functions = new ArrayList<>();
        for (FunctionEntry entry : functionEntries) {
            if (entry.isEnabled()) {
                functions.add(new GraphFunction(entry.getExpression(), entry.getColor()));
            }
        }
        graphPanel.setFunctions(functions);
        graphPanel.repaint();
    }
}