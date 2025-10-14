package lib.panel;
import javax.swing.*;

import lib.function.Function;
import lib.function.functions.ExpressionFunction;
import lib.function.functions.NumberSetFunction;
import lib.function.functions.RangeFunction;
import lib.panel.entry.FunctionEntry;
import lib.panel.entry.PlottableFunctionEntry;
import lib.panel.entry.entries.ExpressionFunctionEntry;
import lib.panel.entry.entries.RangeFunctionEntry;
import lib.panel.entry.entries.NumberSetFunctionEntry;
import lib.panel.plot.PlotPanel;
import lib.parser.InputParser;
import lib.parser.InputParser.ParseResult;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Side panel containing the function list and plot controls
 */
public class EntryPanel extends JPanel {

    private JPanel functionListPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton plotButton;
    private List<FunctionEntry<?>> functionItems;
    private PlotPanel plotPanel;
    
    /**
     * Constructor
     * @param plotPanel The PlotPanel to interact with
     */
    public EntryPanel(PlotPanel plotPanel) {
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

        // Validate input
        String input = inputField.getText().trim();
        if (!validateInput(input)) return;
        
        // Parse input using InputParser
        ParseResult parseResult;
        try {
            parseResult = InputParser.parse(input);
        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(this, 
                e.getMessage(), 
                "Parse Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Handle different input types
        switch (parseResult.getType()) {
            case EXPRESSION:
                addExpressionFromParseResult(parseResult);
                break;
            case RANGE:
                addRangeFromParseResult(parseResult);
                break;
            case NUMBER_SET:
                addNumberSetFromParseResult(parseResult);
                break;
            default:
                JOptionPane.showMessageDialog(this, 
                    "Unsupported input type", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                break;
        }
        
        // Clear input field
        inputField.setText("");
    }
    
    /**
     * Add a function from a ParseResult
     * @param parseResult The parsed result containing function information
     */
    private void addExpressionFromParseResult(ParseResult parseResult) {
        
        String name = parseResult.getName();
        String expression = parseResult.getExpression();
        
        // Validate expression
        if (!validateInput(expression)) return;
        
        // Create Function object using factory
        Color randomColor = generateRandomColor();
        ExpressionFunction function = parseResult.hasName()
            ? new ExpressionFunction(expression, randomColor, name)
            : new ExpressionFunction(expression, randomColor);
        
        plotPanel.addFunction(function);

        // Create and add UI entry using factory
        final PlottableFunctionEntry<?>[] entryHolder = new PlottableFunctionEntry<?>[1];
        entryHolder[0] = new ExpressionFunctionEntry(
            function,
            this::onVisibilityChanged,
            () -> onFunctionRemove(function, entryHolder[0]),
            () -> onFunctionEdit(function, entryHolder[0])
        );
        functionItems.add(entryHolder[0]);
        addEntryToPanel(entryHolder[0]);
    
        // Refresh UI
        refreshPanels();
    }

    private void addRangeFromParseResult(ParseResult parseResult) {

        String name = parseResult.getName();
        String expression = parseResult.getExpression();

        // Validate expression
        if (!validateInput(expression)) return;

        // Create Function object using factory
        RangeFunction function = parseResult.hasName()
            ? new RangeFunction(expression, name)
            : new RangeFunction(expression);

        // Create and add UI entry using factory
        final FunctionEntry<?>[] entryHolder = new FunctionEntry<?>[1];
        entryHolder[0] = new RangeFunctionEntry(
            function,
            this::onVisibilityChanged,
            () -> onFunctionRemove(function, entryHolder[0]),
            () -> onFunctionEdit(function, entryHolder[0])
        );
        functionItems.add(entryHolder[0]);
        addEntryToPanel(entryHolder[0]);

        // Refresh UI
        refreshPanels();
    }

    private void addNumberSetFromParseResult(ParseResult parseResult) {

        String name = parseResult.getName();
        String expression = parseResult.getExpression();

        // Validate expression
        if (!validateInput(expression)) return;

        // Create Function object using factory
        NumberSetFunction function = parseResult.hasName()
            ? new NumberSetFunction(expression, name)
            : new NumberSetFunction(expression);

        // Create and add UI entry using factory
        final FunctionEntry<?>[] entryHolder = new FunctionEntry<?>[1];
        entryHolder[0] = new NumberSetFunctionEntry(
            function,
            this::onVisibilityChanged,
            () -> onFunctionRemove(function, entryHolder[0]),
            () -> onFunctionEdit(function, entryHolder[0])
        );
        functionItems.add(entryHolder[0]);
        addEntryToPanel(entryHolder[0]);

        // Refresh UI
        refreshPanels();
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
     * Add a FunctionEntry to the panel (before the glue)
     * @param entry The entry to add
     */
    private void addEntryToPanel(FunctionEntry<?> entry) {

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
    private void onFunctionRemove(Function function, FunctionEntry<?> entry) {

        plotPanel.removeFunction(function);
        functionItems.remove(entry);
        removeEntryFromPanel(entry);
        refreshPanels();
    }
    
    /**
     * Remove a FunctionEntry and its spacing from the panel
     * @param entry The entry to remove
     */
    private void removeEntryFromPanel(FunctionEntry<?> entry) {

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
    private int findEntryIndex(FunctionEntry<?> entry) {

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
    private void onFunctionEdit(Function function, FunctionEntry<?> entry) {

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
