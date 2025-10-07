package lib.graph;
import java.awt.Color;

public class GraphFunction {
    
    private String expression;
    private Color color;
    // optional display name for the function (e.g. f, g, h)
    private String name;
    // intersection mode: if true, lhsExpr and rhsExpr are used
    private boolean intersection = false;
    private String lhsExpr;
    private String rhsExpr;
    // region mode: if true, represents a region like f(x)>=g(x)
    private boolean region = false;
    private String regionOperator; // ">=", "<=", ">", "<"
    
    /**
     * Constructor to create a graph function
     * @param expression The function expression
     * @param color The color to draw this function
     */
    public GraphFunction(String expression, Color color) {
        this.expression = expression;
        this.color = color;
    }

    /**
     * Create a GraphFunction that represents intersections between two expressions.
     */
    public static GraphFunction intersection(String lhs, String rhs, Color color) {
        GraphFunction gf = new GraphFunction(null, color);
        gf.intersection = true;
        gf.lhsExpr = lhs;
        gf.rhsExpr = rhs;
        return gf;
    }

    /**
     * Create a GraphFunction that represents a region (e.g., f(x)>=g(x))
     * @param lhs Left-hand side expression
     * @param operator Comparison operator: ">=", "<=", ">", "<"
     * @param rhs Right-hand side expression
     * @param color Color for the region border and fill
     */
    public static GraphFunction region(String lhs, String operator, String rhs, Color color) {
        GraphFunction gf = new GraphFunction(null, color);
        gf.region = true;
        gf.lhsExpr = lhs;
        gf.rhsExpr = rhs;
        gf.regionOperator = operator;
        return gf;
    }

    /**
     * Optional: set a name for this function (useful for intersection definitions like h(x)=(...=...))
     */
    public void setName(String name) { this.name = name; }

    /**
     * Get the optional name for this function
     */
    public String getName() { return name; }

    public boolean isIntersection() { return intersection; }
    public String getLhsExpr() { return lhsExpr; }
    public String getRhsExpr() { return rhsExpr; }
    
    public boolean isRegion() { return region; }
    public String getRegionOperator() { return regionOperator; }
    
    /**
     * Get the expression
     * @return The expression string
     */
    public String getExpression() {
        return expression;
    }
    
    /**
     * Get the color
     * @return The color
     */
    public Color getColor() {
        return color;
    }
}
