package lib.ui.component.factory;

import lib.core.factory.FunctionFactory;
import lib.core.parser.FunctionParser;
import lib.model.domain.Parameter;
import lib.model.function.base.PlottableFunction;
import lib.model.function.definition.ConstantFunction;
import lib.model.function.definition.SetFunction;
import lib.ui.component.entry.AbstractFunctionEntry;
import lib.ui.component.entry.BaseFunctionEntry;
import lib.ui.component.entry.ConstantFunctionEntry;
import lib.ui.component.entry.PlottableFunctionEntry;
import lib.ui.component.utility.FunctionColorManager;
import lib.ui.panel.FunctionPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class FunctionEntryFactory {
    
    private final FunctionFactory functionFactory;
    private final FunctionPanel parent;
    private final FunctionColorManager colorManager;
    
    public FunctionEntryFactory(FunctionFactory functionFactory, FunctionPanel parent) {
        this.functionFactory = functionFactory;
        this.parent = parent;
        this.colorManager = new FunctionColorManager();
    }
    
    public AbstractFunctionEntry createEntry(String expression) {
        expression = expression.trim();
        
        if (FunctionParser.isParameter(expression)) {
            return createParameterEntry(expression);
        }
        
        if (FunctionParser.isSet(expression)) {
            return createSetEntry(expression);
        }
        
        Color color = colorManager.getNextColor();
        PlottableFunction function = functionFactory.createFunction(expression, color);
        
        if (function != null) {
            return new PlottableFunctionEntry(function, parent);
        }
        
        return null;
    }
    
    private ConstantFunctionEntry createParameterEntry(String expression) {
        Parameter param = FunctionParser.parseParameter(expression);
        if (param == null) return null;
        
        ConstantFunction constFunc = new ConstantFunction(
            param.getName(),
            param.getMinValue(),
            param.getMaxValue(),
            param.getCurrentValue(),
            param.isDiscrete()
        );
        
        return new ConstantFunctionEntry(expression, constFunc, parent);
    }
    
    private BaseFunctionEntry createSetEntry(String expression) {
        if (FunctionParser.isExplicitSet(expression)) {
            Object[] result = FunctionParser.parseExplicitSet(expression);
            if (result != null && result.length == 2) {
                String name = (String) result[0];
                double[] values = (double[]) result[1];
                List<Double> valueList = new ArrayList<>();
                for (double v : values) {
                    valueList.add(v);
                }
                SetFunction setFunc = new SetFunction(name, valueList);
                return new BaseFunctionEntry(expression, setFunc, parent);
            }
        } else if (FunctionParser.isRangeSet(expression)) {
            Object[] result = FunctionParser.parseRangeSet(expression);
            if (result != null && result.length == 3) {
                String name = (String) result[0];
                int min = (Integer) result[1];
                int max = (Integer) result[2];
                List<Double> valueList = new ArrayList<>();
                for (int i = min; i <= max; i++) {
                    valueList.add((double) i);
                }
                SetFunction setFunc = new SetFunction(name, valueList);
                return new BaseFunctionEntry(expression, setFunc, parent);
            }
        }
        return null;
    }
    
    public boolean isValidExpression(String expression) {
        return createEntry(expression) != null;
    }
}