package lib.panel;
import javax.swing.*;

import lib.Function;
import lib.panel.plot.PlotPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Side panel containing the function list and plot controls
 */
public class FunctionPanel extends JPanel {

    private JPanel functionListPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton plotButton;
    private List<FunctionEntry> functionItems;
    private PlotPanel plotPanel;
    
    /**
     * Constructor
     * @param plotPanel The PlotPanel to interact with
     */
    public FunctionPanel(PlotPanel plotPanel) {
        this.plotPanel = plotPanel;
        this.functionItems = new ArrayList<>();
        
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(300, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        layoutComponents();
    }
    
    /**
     * Initialize components
     */
    private void initComponents() {

        // Function list panel (will contain FunctionListItem components)
        functionListPanel = new JPanel();
        functionListPanel.setLayout(new BoxLayout(functionListPanel, BoxLayout.Y_AXIS));
        functionListPanel.setBackground(Color.WHITE);
        
        // Add initial glue to push items to the top
        functionListPanel.add(Box.createVerticalGlue());
        
        // Scroll pane for the function list
        scrollPane = new JScrollPane(functionListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Functions"));
        
        // Input field for new functions
        inputField = new JTextField("");
        
        // Plot button
        plotButton = new JButton("Plot");
        plotButton.addActionListener(e -> addFunction());
    }
    
    /**
     * Layout components
     */
    private void layoutComponents() {

        // Bottom panel with input and plot button
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        inputPanel.add(inputField, BorderLayout.CENTER);
        
        bottomPanel.add(inputPanel, BorderLayout.NORTH);
        bottomPanel.add(plotButton, BorderLayout.SOUTH);
        
        // Add to main panel
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Add a new function from the input field
     */
    private void addFunction() {

        // Get and validate input
        String expression = inputField.getText().trim();
        
        if (!validateInput(expression)) return;
        
        // Create and add function
        Function function = createFunction(expression);
        plotPanel.addFunction(function);
        
        // Create and add UI entry
        FunctionEntry entry = createFunctionEntry(function);
        functionItems.add(entry);
        addEntryToPanel(entry);
        
        // Refresh UI
        refreshPanels();
        inputField.setText("");
    }
    
    /**
     * Validate the input expression
     * @param expression The expression to validate
     * @return true if valid, false otherwise
     */
    private boolean validateInput(String expression) {

        if (!expression.isEmpty()) return true;
        JOptionPane.showMessageDialog(this, 
            "Please enter a function expression", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    /**
     * Create a new Function object with a random color
     * @param expression The function expression
     * @return The created Function
     */
    private Function createFunction(String expression) {

        Color randomColor = generateRandomColor();
        return new Function(expression, randomColor);
    }
    
    /**
     * Create a new Function object with a random color
     * @param expression The function expression
     * @return The created Function
     */
    private Function createFunction(String expression, String name) {

        Color randomColor = generateRandomColor();
        return new Function(expression, randomColor, name);
    }
    
    /**
     * Create a FunctionEntry UI component for the given function
     * @param function The function to create an entry for
     * @return The created FunctionEntry
     */
    private FunctionEntry createFunctionEntry(Function function) {

        final FunctionEntry[] entryHolder = new FunctionEntry[1];
        
        entryHolder[0] = new FunctionEntry(
            function,
            this::onVisibilityChanged,
            () -> onFunctionRemove(function, entryHolder[0]),
            () -> onFunctionEdit(function, entryHolder[0])
        );
        
        return entryHolder[0];
    }
    
    /**
     * Add a FunctionEntry to the panel (before the glue)
     * @param entry The entry to add
     */
    private void addEntryToPanel(FunctionEntry entry) {

        int glueIndex = functionListPanel.getComponentCount() - 1;
        functionListPanel.add(entry, glueIndex);
        functionListPanel.add(Box.createRigidArea(new Dimension(0, 5)), glueIndex + 1);
    }
    
    /**
     * Callback when function visibility is changed
     */
    private void onVisibilityChanged() { plotPanel.repaint(); }
    
    /**
     * Callback when a function is removed
     * @param function The function to remove
     * @param entry The UI entry to remove
     */
    private void onFunctionRemove(Function function, FunctionEntry entry) {

        plotPanel.removeFunction(function);
        functionItems.remove(entry);
        removeEntryFromPanel(entry);
        refreshPanels();
    }
    
    /**
     * Remove a FunctionEntry and its spacing from the panel
     * @param entry The entry to remove
     */
    private void removeEntryFromPanel(FunctionEntry entry) {

        int index = findEntryIndex(entry);
        
        if (index >= 0) {

            functionListPanel.remove(index); // Remove the FunctionEntry
            if (index < functionListPanel.getComponentCount() - 1) functionListPanel.remove(index);
        }
    }
    
    /**
     * Find the index of a FunctionEntry in the panel
     * @param entry The entry to find
     * @return The index, or -1 if not found
     */
    private int findEntryIndex(FunctionEntry entry) {

        Component[] components = functionListPanel.getComponents();
        for (int i = 0; i < components.length; i++)
            if (components[i] == entry) return i;
        return -1;
    }
    
    /**
     * Callback when a function is edited
     * @param function The function to edit
     * @param entry The UI entry to update
     */
    private void onFunctionEdit(Function function, FunctionEntry entry) {

        // Get the new expression from the input field in the entry
        String newExpression = entry.getInputFieldText();
        
        if (newExpression != null && !newExpression.trim().isEmpty()) {

            function.setExpression(newExpression.trim());
            entry.updateExpression();
            plotPanel.repaint();
        }
    }
    
    /**
     * Refresh all panels
     */
    private void refreshPanels() {

        functionListPanel.revalidate();
        functionListPanel.repaint();
        plotPanel.repaint();
    }
    
    /**
     * Generate a random color for a new function
     */
    private Color generateRandomColor() {

        Color[] colors = {
            new Color(0, 102, 204),    // Blue
            new Color(204, 0, 0),      // Red
            new Color(0, 153, 51),     // Green
            new Color(204, 102, 0),    // Orange
            new Color(102, 0, 204),    // Purple
            new Color(0, 153, 153),    // Teal
            new Color(204, 0, 102),    // Magenta
            new Color(102, 102, 0)     // Olive
        };
        
        int index = functionItems.size() % colors.length;
        return colors[index];
    }
}
