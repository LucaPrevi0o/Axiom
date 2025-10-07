package lib.function;
import javax.swing.*;

import lib.graph.GraphFunction;
import lib.graph.GraphPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FunctionPanel extends JPanel {
    
    private GraphPanel graphPanel;
    private List<FunctionEntry> functionEntries;
    private java.util.Map<String, String> namedFunctions = new java.util.HashMap<>();
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
        
    // Add a default named function
    addFunction("f(x)=x^2");
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
        // Rebuild named functions map and visible functions list
        namedFunctions.clear();
        List<GraphFunction> functions = new ArrayList<>();
        for (FunctionEntry entry : functionEntries) {
            String expr = entry.getExpression().trim();
            // Detect definitions of the form name(x)=expression
            if (expr.matches("^\\s*([A-Za-z_]\\w*)\\s*\\(\\s*x\\s*\\)\\s*=.*$")) {
                // extract name and rhs
                int eq = expr.indexOf('=');
                String left = expr.substring(0, eq).trim();
                String name = left.substring(0, left.indexOf('(')).trim();
                String rhs = expr.substring(eq + 1).trim();
                namedFunctions.put(name.toLowerCase(), rhs);
                // also add the named function to be plotted using this entry's color
                if (entry.isEnabled()) {
                    // If the RHS itself is an intersection like (a=b), create a named intersection
                    if (rhs.matches("^\\s*\\(.*=.*\\)\\s*$")) {
                        String insideRhs = rhs.substring(1, rhs.length() - 1).trim();
                        int eqIdxRhs = insideRhs.indexOf('=');
                        if (eqIdxRhs > 0) {
                            String leftExpr = insideRhs.substring(0, eqIdxRhs).trim();
                            String rightExpr = insideRhs.substring(eqIdxRhs + 1).trim();
                            GraphFunction gf = lib.graph.GraphFunction.intersection(leftExpr, rightExpr, entry.getColor());
                            gf.setName(name);
                            functions.add(gf);
                        }
                    } else {
                        // plot the function by its RHS expression and set its name
                        GraphFunction gf = new GraphFunction(rhs, entry.getColor());
                        gf.setName(name);
                        functions.add(gf);
                    }
                }
            }
            // Detect intersection request: (expr1=expr2)
            else if (expr.matches("^\\s*\\(.*=.*\\)\\s*$")) {
                // strip surrounding parentheses
                String inside = expr.trim();
                inside = inside.substring(1, inside.length() - 1).trim();
                int eqIdx = inside.indexOf('=');
                if (eqIdx > 0) {
                    String left = inside.substring(0, eqIdx).trim();
                    String right = inside.substring(eqIdx + 1).trim();
                    if (entry.isEnabled()) {
                        functions.add(lib.graph.GraphFunction.intersection(left, right, entry.getColor()));
                    }
                }
            } else {
                if (entry.isEnabled()) {
                    functions.add(new GraphFunction(expr, entry.getColor()));
                }
            }
        }

        // Provide named functions to the graph panel so evaluator can resolve them
        graphPanel.setUserFunctions(namedFunctions);
        graphPanel.setFunctions(functions);
        graphPanel.repaint();
    }

    public java.util.Map<String, String> getNamedFunctions() {
        return namedFunctions;
    }
}