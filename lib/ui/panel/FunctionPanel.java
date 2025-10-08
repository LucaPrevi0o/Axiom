package lib.ui.panel;

import lib.core.FunctionParser;
import lib.core.FunctionFactory;
import lib.model.Parameter;
import lib.ui.component.FunctionColorManager;
import lib.ui.component.FunctionEntry;
import lib.ui.component.ParameterEntry;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for managing function entries and parameters
 * Uses a unified list for both types
 */
public class FunctionPanel extends JPanel {
    
    private final GraphPanel graphPanel;
    private final List<FunctionEntry> functionEntries;
    private final List<Parameter> parameters;
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
        this.parameters = new ArrayList<>();
        this.colorManager = new FunctionColorManager();
        this.entriesPanel = new JPanel();
        this.addButton = new JButton("+ Add Function");
        
        initComponents();
        layoutComponents();
        
        // Set up listener for parameter updates from point dragging
        graphPanel.setParameterUpdateListener(new GraphPanel.ParameterUpdateListener() {
            @Override
            public void onParameterUpdated(String parameterName, double newValue) {
                // Find and update the corresponding parameter entry UI
                for (Component comp : entriesPanel.getComponents()) {
                    if (comp instanceof ParameterEntry) {
                        ParameterEntry entry = (ParameterEntry) comp;
                        if (entry.getParameter().getName().equalsIgnoreCase(parameterName)) {
                            entry.updateSliderValue(newValue);
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
     * Add a new function or parameter
     */
    public void addFunction(String expression) {
        Color color = colorManager.getNextColor();
        
        // Check if this is a parameter definition
        if (FunctionParser.isParameter(expression.trim())) {
            Parameter param = FunctionParser.parseParameter(expression.trim());
            if (param != null) {
                addParameterEntry(param, color);
                return;
            }
        }
        
        // Check if this is a set - sets should not show visual controls
        boolean isSet = FunctionParser.isSet(expression.trim());
        
        // Otherwise, create a regular function entry
        FunctionEntry entry = new FunctionEntry(expression, color, this, !isSet);
        functionEntries.add(entry);
        entriesPanel.add(entry);
        entriesPanel.add(Box.createVerticalStrut(5));
        
        revalidate();
        repaint();
        updateGraph();
    }
    
    /**
     * Add a parameter entry
     */
    private void addParameterEntry(Parameter param, Color color) {
        // Check if parameter already exists
        boolean exists = parameters.stream()
            .anyMatch(p -> p.getName().equalsIgnoreCase(param.getName()));
        
        if (!exists) {
            parameters.add(param);
            
            ParameterEntry entry = new ParameterEntry(param, new ParameterEntry.ParameterChangeListener() {
                @Override
                public void onParameterChanged(Parameter parameter) {
                    updateGraph();
                }
                
                @Override
                public void onParameterDeleted(Parameter parameter) {
                    removeParameter(parameter);
                }
            });
            
            entriesPanel.add(entry);
            entriesPanel.add(Box.createVerticalStrut(5));
            
            revalidate();
            repaint();
            updateGraph();
        }
    }
    
    /**
     * Remove a function
     */
    public void removeFunction(FunctionEntry entry) {
        functionEntries.remove(entry);
        
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
        updateGraph();
    }
    
    /**
     * Convert a FunctionEntry to a ParameterEntry
     */
    public void convertToParameter(FunctionEntry entry, String expression) {
        // Parse the parameter
        Parameter param = FunctionParser.parseParameter(expression);
        if (param == null) {
            return;
        }
        
        // Get the color from the entry before removing it
        Color color = entry.getColor();
        
        // Remove the old function entry
        removeFunction(entry);
        
        // Add the parameter entry
        addParameterEntry(param, color);
    }
    
    /**
     * Remove a parameter
     */
    private void removeParameter(Parameter parameter) {
        parameters.removeIf(p -> p.getName().equalsIgnoreCase(parameter.getName()));
        
        Component[] components = entriesPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof ParameterEntry) {
                ParameterEntry entry = (ParameterEntry) components[i];
                if (entry.getParameter().getName().equalsIgnoreCase(parameter.getName())) {
                    entriesPanel.remove(i); // Remove entry
                    if (i < components.length - 1) {
                        entriesPanel.remove(i); // Remove spacer
                    }
                    break;
                }
            }
        }
        
        revalidate();
        repaint();
        updateGraph();
    }
    
    /**
     * Update graph with all current functions
     */
    public void updateGraph() {
        // IMPORTANT: Update parameters FIRST before creating the factory
        // This ensures the ExpressionEvaluator has the latest parameter values
        // when creating ParametricPointFunction instances
        
        // Build parameter values map
        java.util.Map<String, Double> paramValues = new java.util.HashMap<>();
        java.util.Map<String, Parameter> paramObjects = new java.util.HashMap<>();
        for (Parameter param : parameters) {
            paramValues.put(param.getName().toLowerCase(), param.getCurrentValue());
            paramObjects.put(param.getName().toLowerCase(), param);
        }
        
        // Parse named functions first to update userFunctions
        namedFunctions = new java.util.HashMap<>();
        for (FunctionEntry entry : functionEntries) {
            if (entry.isEnabled() && FunctionParser.isNamedFunction(entry.getExpression())) {
                String name = FunctionParser.extractName(entry.getExpression());
                String rhs = FunctionParser.extractRHS(entry.getExpression());
                if (name != null && rhs != null) {
                    namedFunctions.put(name.toLowerCase(), rhs);
                }
            }
        }
        
        // Update GraphPanel's user functions, parameters, and parameter objects
        graphPanel.setUserFunctions(namedFunctions);
        graphPanel.setParameters(paramValues);
        graphPanel.setParameterObjects(paramObjects);
        
        // NOW create factory with the updated evaluator
        FunctionFactory factory = new FunctionFactory(
            graphPanel.getEvaluator(), 
            graphPanel.getIntersectionFinder()
        );
        
        // Parse function entries using factory (now has correct parameters)
        FunctionParser.ParseResult result = FunctionParser.parseEntries(functionEntries, factory);
        
        graphPanel.setFunctions(result.getFunctions());
        graphPanel.repaint();
    }
    
    public java.util.Map<String, String> getNamedFunctions() {
        return namedFunctions;
    }
}
