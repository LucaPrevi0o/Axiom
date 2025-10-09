package lib.ui.panel;

import lib.core.factory.FunctionFactory;
import lib.core.parser.FunctionParser;
import lib.ui.component.entry.AbstractFunctionEntry;
import lib.ui.component.entry.BaseFunctionEntry;
import lib.ui.component.entry.ConstantFunctionEntry;
import lib.ui.component.entry.PlottableFunctionEntry;
import lib.ui.component.factory.FunctionEntryFactory;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for managing function entries and parameters
 * Refactored to use new AbstractFunctionEntry hierarchy
 */
public class FunctionPanel extends JPanel {
    
    private final GraphPanel graphPanel;
    private final List<AbstractFunctionEntry> functionEntries;  // Changed to AbstractFunctionEntry
    private final JPanel entriesPanel;
    private final JButton addButton;
    private FunctionEntryFactory entryFactory;  // New: factory for creating entries
    
    private java.util.Map<String, String> namedFunctions = new java.util.HashMap<>();
    
    /**
     * Constructor
     * @param graphPanel Graph panel to update
     */
    public FunctionPanel(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        this.functionEntries = new ArrayList<>();
        this.entriesPanel = new JPanel();
        this.addButton = new JButton("+ Add Function");
        
        initComponents();
        layoutComponents();
        
        // Set up listener for parameter updates from point dragging
        graphPanel.setParameterUpdateListener(new GraphPanel.ParameterUpdateListener() {
            @Override
            public void onParameterUpdated(String parameterName, double newValue) {
                // Find and update the corresponding constant entry UI
                for (Component comp : entriesPanel.getComponents()) {
                    if (comp instanceof ConstantFunctionEntry) {
                        ConstantFunctionEntry entry = (ConstantFunctionEntry) comp;
                        if (entry.getConstantFunction().getName().equalsIgnoreCase(parameterName)) {
                            entry.setValue(newValue);
                            break;
                        }
                    }
                }
            }
        });
        
        // Add default function
        addFunction("f(x)=x^2");
    }
    
    /**
     * Initialize components
     */
    private void initComponents() {
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
     * Add a new function or parameter using the new entry system
     */
    public void addFunction(String expression) {
        // Initialize factory if needed
        if (entryFactory == null) {
            FunctionFactory funcFactory = new FunctionFactory(
                graphPanel.getEvaluator(),
                graphPanel.getIntersectionFinder()
            );
            entryFactory = new FunctionEntryFactory(funcFactory, this);
        }
        
        // Use factory to create appropriate entry type
        AbstractFunctionEntry entry = entryFactory.createEntry(expression);
        if (entry != null) {
            functionEntries.add(entry);
            entriesPanel.add(entry);
            entriesPanel.add(Box.createVerticalStrut(5));
            
            revalidate();
            repaint();
            updateGraph();
        }
    }
    
    /**
     * Remove a function entry (new API for AbstractFunctionEntry)
     */
    public void removeFunctionEntry(AbstractFunctionEntry entry) {
        removeFunctionEntryInternal(entry);
        updateGraph();
    }
    
    /**
     * Remove an abstract function entry without updating the graph
     * Used internally when we know updateGraph will be called later
     */
    private void removeFunctionEntryInternal(AbstractFunctionEntry entry) {
        // Find and remove the entry from the panel
        Component[] components = entriesPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == entry) {
                entriesPanel.remove(i);
                // Also remove the spacer if it exists
                if (i < entriesPanel.getComponentCount()) {
                    entriesPanel.remove(i);
                }
                break;
            }
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * Update graph with all current functions
     * Refactored to work with new AbstractFunctionEntry hierarchy
     */
    public void updateGraph() {
        // Build parameter values map from ConstantFunctionEntry instances
        java.util.Map<String, Double> paramValues = new java.util.HashMap<>();
        java.util.Map<String, lib.model.function.definition.ConstantFunction> constantFunctions = new java.util.HashMap<>();
        
        for (AbstractFunctionEntry entry : functionEntries) {
            if (entry instanceof ConstantFunctionEntry) {
                ConstantFunctionEntry constEntry = (ConstantFunctionEntry) entry;
                lib.model.function.definition.ConstantFunction constant = constEntry.getConstantFunction();
                paramValues.put(constant.getName().toLowerCase(), constant.getCurrentValue());
                constantFunctions.put(constant.getName().toLowerCase(), constant);
            }
        }
        
        // Parse named functions first to update userFunctions
        namedFunctions = new java.util.HashMap<>();
        for (AbstractFunctionEntry entry : functionEntries) {
            if (entry instanceof PlottableFunctionEntry) {
                PlottableFunctionEntry plotEntry = (PlottableFunctionEntry) entry;
                if (plotEntry.isEnabled() && FunctionParser.isNamedFunction(entry.getExpression())) {
                    String name = FunctionParser.extractName(entry.getExpression());
                    String rhs = FunctionParser.extractRHS(entry.getExpression());
                    if (name != null && rhs != null) {
                        namedFunctions.put(name.toLowerCase(), rhs);
                    }
                }
            }
        }
        
        // Update GraphPanel's user functions and parameters
        graphPanel.setUserFunctions(namedFunctions);
        graphPanel.setParameters(paramValues);
        
        // Build lists of plottable functions and sets
        java.util.List<lib.model.function.base.PlottableFunction> plottableFunctions = new java.util.ArrayList<>();
        java.util.List<lib.model.function.definition.SetFunction> setsList = new java.util.ArrayList<>();
        
        for (AbstractFunctionEntry entry : functionEntries) {
            if (entry instanceof PlottableFunctionEntry) {
                PlottableFunctionEntry plotEntry = (PlottableFunctionEntry) entry;
                if (plotEntry.isEnabled()) {
                    plottableFunctions.add(plotEntry.getPlottableFunction());
                }
            } else if (entry instanceof BaseFunctionEntry) {
                // Check if it's a set
                lib.model.function.base.Function func = entry.getFunction();
                if (func instanceof lib.model.function.definition.SetFunction) {
                    lib.model.function.definition.SetFunction set = (lib.model.function.definition.SetFunction) func;
                    setsList.add(set);
                }
            }
        }
        
        graphPanel.setFunctions(plottableFunctions, setsList);
        graphPanel.repaint();
    }
    
    public java.util.Map<String, String> getNamedFunctions() {
        return namedFunctions;
    }
}
