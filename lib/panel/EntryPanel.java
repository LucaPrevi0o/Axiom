package lib.panel;
import javax.swing.*;

import lib.panel.plot.PlotPanel;
import lib.parser.InputParser;
import lib.parser.InputParser.ParseResult;

import java.awt.*;

/**
 * Side panel containing the function list and plot controls
 */
public class EntryPanel extends JPanel {

    private JPanel functionListPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton plotButton;
    private EntryManager entryManager;
    
    /**
     * Constructor
     * @param plotPanel The PlotPanel to interact with
     */
    public EntryPanel(PlotPanel plotPanel) {
        
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(300, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        layoutComponents();

        this.entryManager = new EntryManager(this, plotPanel, functionListPanel);
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
        if (!entryManager.validateInput(input)) return;
        
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
                entryManager.addExpressionFromParseResult(parseResult);
                break;
            case RANGE:
                entryManager.addRangeFromParseResult(parseResult);
                break;
            case NUMBER_SET:
                entryManager.addNumberSetFromParseResult(parseResult);
                break;
            case CONSTANT:
                entryManager.addConstantFromParseResult(parseResult);
                break;
            case POINT:
                entryManager.addPointFromParseResult(parseResult);
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
}
