package lib.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import lib.function.Function;
import lib.function.functions.*;
import lib.panel.entry.FunctionEntry;
import lib.panel.entry.PlottableFunctionEntry;
import lib.panel.entry.entries.*;
import lib.panel.plot.PlotPanel;
import lib.parser.InputParser.ParseResult;

/**
 * Manages the list of function entries and their interactions with the PlotPanel
 */
public class EntryManager {

    public EntryPanel entryPanel;
    public PlotPanel plotPanel;
    public JPanel functionListPanel;
    public java.util.List<FunctionEntry<?>> functionItems;

    /**
     * Constructor
     * @param entryPanel The EntryPanel containing the function list
     * @param plotPanel The PlotPanel to interact with
     * @param functionListPanel The JPanel holding the function entries
     */
    public EntryManager(EntryPanel entryPanel, PlotPanel plotPanel, JPanel functionListPanel) {

        this.entryPanel = entryPanel;
        this.plotPanel = plotPanel;
        this.functionListPanel = functionListPanel;
        this.functionItems = new java.util.ArrayList<>();
    }
    
    /**
     * Validate the input expression
     * @param expression The expression to validate
     * @return true if valid, false otherwise
     */
    public boolean validateInput(String expression) {

        if (!expression.isEmpty()) return true;
        JOptionPane.showMessageDialog(entryPanel, 
            "Please enter a function expression", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
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
     * Update the constants map in the PlotPanel based on current ConstantFunction entries
     */
    private void updateConstants() {

        java.util.Map<String, Double> constantsMap = new java.util.HashMap<>();
        
        for (FunctionEntry<?> entry : functionItems)
            if (entry instanceof ConstantFunctionEntry) {

                ConstantFunctionEntry constantEntry = (ConstantFunctionEntry)entry;
                ConstantFunction constantFunction = constantEntry.getFunction();
                
                // Only add if it has a name
                if (constantFunction.getName() != null) constantsMap.put(constantFunction.getName(), constantFunction.getValue());
            }
        
        plotPanel.setConstants(constantsMap);
    }
    
    /**
     * Callback when function visibility is changed
     */
    private void onVisibilityChanged() {

        updateConstants();
        plotPanel.repaint(); 
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

        updateConstants();
        functionListPanel.revalidate();
        functionListPanel.repaint();
        plotPanel.repaint();
    }
    
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
     * Add a function from a ParseResult
     * @param parseResult The parsed result containing function information
     */
    public void addExpressionFromParseResult(ParseResult parseResult) {
        
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

    /** Add a RangeFunction from a ParseResult
     * @param parseResult The parsed result containing function information
     */
    public void addRangeFromParseResult(ParseResult parseResult) {

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

    /** Add a NumberSetFunction from a ParseResult
     * @param parseResult The parsed result containing function information
     */
    public void addNumberSetFromParseResult(ParseResult parseResult) {

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

    /** Add a NumberSetFunction from a ParseResult
     * @param parseResult The parsed result containing function information
     */
    public void addConstantFromParseResult(ParseResult parseResult) {

        String name = parseResult.getName();
        String expression = parseResult.getExpression();

        // Validate expression
        if (!validateInput(expression)) return;

        // Create Function object using factory
        ConstantFunction function = parseResult.hasName()
            ? new ConstantFunction(expression, name)
            : new ConstantFunction(expression);

        // Create and add UI entry using factory
        final FunctionEntry<?>[] entryHolder = new FunctionEntry<?>[1];
        entryHolder[0] = new ConstantFunctionEntry(
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
}
