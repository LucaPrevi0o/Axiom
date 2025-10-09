# Axiom - Graphing Calculator

A powerful Java-based graphing calculator with support for mathematical functions, parametric equations, intersection analysis, and inequality regions. Built with a clean, extensible architecture using modern software design patterns.

## What You Can Do

### Function Types

- **Standard Functions**: Plot any mathematical expression as y = f(x)
  - Example: `x^2 + 2*x - 3`
  - Example: `sin(x) * cos(x)`

- **Named Functions**: Define reusable functions with names
  - Example: `f(x) = x^2 + 1`
  - Use in other expressions: `2*f(x)` or `f(x) + g(x)`

- **Parametric Points**: Plot points with parametric coordinates
  - Example: `P=(a, sin(a))` - point moves as parameter 'a' changes
  - Example: `Q=(cos(t), sin(t))` - traces a circle as 't' varies

- **Point Sets**: Plot discrete sets of points
  - Example: `{(0,0), (1,1), (2,4)}`

- **Intersections**: Find where functions meet
  - Example: `(x^2 = 2*x + 1)` - shows only intersection points
  - Named: `g(x) = (sin(x) = x/2)`

- **Inequality Regions**: Visualize areas where inequalities hold
  - Example: `(x^2 >= 2*x)` - shaded region where x² ≥ 2x
  - Supports: `>=`, `<=`, `>`, `<`

### Value Definitions

- **Parameters with Sliders**: Interactive values you can adjust
  - Continuous: `a=[0:10]` - decimal values from 0 to 10
  - Discrete: `n=[1;5]` - only integer values 1, 2, 3, 4, 5

- **Value Sets**: Define collections of values
  - Explicit: `A={1, 2, 3, 4}` - list specific values
  - Range: `B={1:100}` - all integers from 1 to 100
  - Sets can be referenced in other expressions

### Interactive Features

- **Zoom**: Mouse wheel to zoom in/out (zooms toward cursor position)
- **Pan**: Click and drag to move the view
- **Function Controls**: Enable/disable, change colors, edit expressions
- **Real-time Updates**: Graph updates as you type or adjust sliders
- **Adaptive Sampling**: Smooth curves at any zoom level

### Supported Operators and Functions

- **Arithmetic**: `+`, `-`, `*`, `/`, `^` (power)
- **Trigonometric**: `sin`, `cos`, `tan`
- **Logarithmic**: `log` (base 10), `ln` (natural logarithm)
- **Other**: `sqrt`, `abs`
- **Constants**: `pi`, `e`

## Building and Running

```bash
# Compile the project
./jmake

# Compile and run
./jmake run

# Create executable JAR
./jmake jar

# Clean build artifacts
./jmake clean
```

## Package Structure

```
lib/
├── constants/                          # Application-wide constants
│   ├── GraphConstants.java             # Viewport, zoom defaults
│   ├── MathConstants.java              # Numerical precision values
│   ├── RenderingConstants.java         # Colors, line styles, sampling
│   └── UIConstants.java                # UI dimensions, colors
│
├── core/                               # Core business logic
│   ├── evaluation/
│   │   └── ExpressionEvaluator.java    # Evaluates parsed expressions
│   ├── factory/
│   │   └── FunctionFactory.java        # Creates appropriate function instances
│   └── parser/
│       ├── ExpressionParser.java       # Parses mathematical expressions
│       └── FunctionParser.java         # Parses function definitions
│
├── model/                              # Data models and domain logic
│   ├── domain/
│   │   ├── GraphBounds.java            # Coordinate system and transformations
│   │   ├── Parameter.java              # Slider-controlled parameter
│   │   └── ViewportManager.java        # Zoom and pan operations
│   └── function/
│       ├── base/
│       │   ├── BaseFunction.java       # Abstract base for all functions
│       │   └── PlottableFunction.java  # Abstract base for renderable functions
│       ├── composite/
│       │   ├── IntersectionFunction.java   # Intersection points (f=g)
│       │   └── RegionFunction.java         # Inequality regions (f>=g)
│       ├── definition/
│       │   └── SetFunction.java        # Value sets (not plottable)
│       ├── expression/
│       │   └── ExpressionFunction.java # Standard y=f(x) functions
│       └── geometric/
│           ├── ParametricPointFunction.java    # Parametric points
│           └── PointSetFunction.java           # Discrete point sets
│
├── rendering/                          # Rendering pipeline
│   ├── ExpressionFormatter.java        # LaTeX/HTML expression formatting
│   ├── GraphRenderer.java              # Main rendering coordinator
│   ├── IntersectionFinder.java         # Numerical intersection algorithm
│   └── pipeline/
│       ├── AxisRenderer.java           # X/Y axes with tick marks
│       ├── FunctionPlotter.java        # Function curve plotting
│       ├── GridRenderer.java           # Background grid
│       ├── RegionRenderer.java         # Filled inequality regions
│       └── TickCalculator.java         # Tick mark positioning
│
├── ui/                                 # User interface
│   ├── GraphingCalculator.java         # Main application window
│   ├── component/
│   │   ├── FunctionColorManager.java   # Color assignment system
│   │   ├── FunctionEntry.java          # Single function UI row
│   │   ├── ParameterEntry.java         # Parameter with slider UI
│   │   ├── ParameterSlider.java        # Slider component
│   │   └── SimpleDocumentListener.java # Document change helper
│   └── panel/
│       ├── FunctionPanel.java          # Left panel (function list)
│       └── GraphPanel.java             # Right panel (graph display)
│
└── util/                               # Utility classes
    ├── FormattingUtils.java            # Number formatting, tick calculation
    ├── HtmlEscaper.java                # HTML special character escaping
    └── ValidationUtils.java            # Value validation helpers
```

## Core Components

### Function Hierarchy

The application uses an inheritance-based architecture to handle different function types cleanly:

```
BaseFunction (abstract)
├── PlottableFunction (abstract) - Functions that can be rendered
│   ├── ExpressionFunction - Standard y = f(x) curves
│   ├── ParametricPointFunction - Points with parametric coordinates
│   ├── PointSetFunction - Fixed discrete points
│   ├── IntersectionFunction - Intersection points between functions
│   └── RegionFunction - Inequality region boundaries
└── SetFunction - Value collections (not rendered)
```

**Key Classes:**

#### `BaseFunction`
- **Purpose**: Root of the function hierarchy
- **Responsibilities**: 
  - Stores function name and enabled state
  - Defines common interface for all function-like entities
  - Abstract `isPlottable()` distinguishes renderable vs. definition-only
- **Pattern**: Template Method

#### `PlottableFunction`
- **Purpose**: Base for all renderable functions
- **Responsibilities**:
  - Manages display color
  - Handles point computation and caching
  - Template method: subclasses implement `computePoints()`
- **Key Methods**:
  - `getPoints(bounds, width, height)` - Returns cached or recomputed points
  - `computePoints()` - Abstract: subclasses define point generation
  - `invalidateCache()` - Forces recomputation on parameter change
- **Pattern**: Template Method, Caching

#### `ExpressionFunction`
- **Purpose**: Standard mathematical function (y = f(x))
- **Responsibilities**:
  - Evaluates expression for range of x values
  - Implements adaptive sampling based on zoom level
  - Handles discontinuities gracefully
- **Algorithm**: Adaptive sampling (50-5000 samples based on screen width and zoom)

#### `IntersectionFunction`
- **Purpose**: Displays points where two functions intersect
- **Responsibilities**:
  - Uses numerical methods to find intersection points
  - Delegates to `IntersectionFinder` for bisection algorithm
- **Algorithm**: Bisection method with deduplication

#### `RegionFunction`
- **Purpose**: Visualizes inequality regions
- **Responsibilities**:
  - Computes boundary curve
  - Stores operator type for renderer
  - Supports `>=`, `<=`, `>`, `<`
- **Rendering**: Boundary drawn as curve, region filled with semi-transparent color

#### `SetFunction`
- **Purpose**: Non-plottable value container
- **Responsibilities**:
  - Stores collection of values (explicit or range)
  - Can be referenced by parametric functions
  - Not directly rendered
- **Types**: Explicit `{1,2,3}` or range `{1:10}`

### Parsing and Evaluation

#### `FunctionParser`
- **Purpose**: Analyzes function entry syntax
- **Responsibilities**:
  - Detects function type from pattern
  - Separates plottable functions, sets, and parameters
  - Returns `ParseResult` with typed lists
- **Patterns Detected**:
  - Named functions: `f(x)=...`
  - Intersections: `(...=...)`
  - Regions: `(...>=...)`
  - Sets: `{...}` or `{...:...}`
  - Parameters: `[...:...]` or `[...;...]`
  - Points: `P=(...,...)`

#### `ExpressionParser`
- **Purpose**: Parses mathematical expressions into evaluable form
- **Responsibilities**:
  - Tokenizes and parses expressions
  - Handles operators, functions, variables
  - Supports user-defined function expansion
- **Algorithm**: Recursive descent parser
- **Supports**: `+`, `-`, `*`, `/`, `^`, functions, parentheses, variables

#### `ExpressionEvaluator`
- **Purpose**: Evaluates parsed expressions to numbers
- **Responsibilities**:
  - Takes expression and variable values
  - Returns numeric result
  - Handles parameters and user-defined functions
- **Integration**: Works with `ExpressionParser` for evaluation

#### `FunctionFactory`
- **Purpose**: Creates appropriate Function instances
- **Responsibilities**:
  - Analyzes expression patterns
  - Instantiates correct concrete class
  - Encapsulates construction logic
- **Pattern**: Factory Method
- **Returns**: Appropriate subclass of `PlottableFunction` or `SetFunction`

### Rendering System

#### `GraphRenderer`
- **Purpose**: Main rendering coordinator
- **Responsibilities**:
  - Orchestrates rendering pipeline
  - Delegates to specialized renderers
  - Manages rendering order (grid → axes → functions → regions)
- **Pipeline**:
  1. `GridRenderer` - Background grid
  2. `AxisRenderer` - X/Y axes with labels
  3. `FunctionPlotter` - Function curves
  4. `RegionRenderer` - Filled regions
- **Pattern**: Coordinator/Pipeline

#### `IntersectionFinder`
- **Purpose**: Finds function intersection points numerically
- **Responsibilities**:
  - Implements bisection algorithm
  - Detects sign changes between samples
  - Deduplicates nearby roots
- **Algorithm**: 
  - Samples difference function at regular intervals
  - Uses bisection on sign changes
  - Precision: 1e-8, max 40 iterations per root
- **Performance**: Adaptive sampling (200-1000 samples based on screen width)

#### `FunctionPlotter`
- **Purpose**: Renders function curves
- **Responsibilities**:
  - Converts graph points to screen coordinates
  - Draws continuous curves or discrete points
  - Handles out-of-bounds points
- **Rendering**: 
  - Continuous: Connected line segments
  - Discrete: Individual point markers

#### `RegionRenderer`
- **Purpose**: Renders filled inequality regions
- **Responsibilities**:
  - Fills area between boundary and edge
  - Handles different operators (`>=` fills above, `<=` fills below)
  - Uses semi-transparent fill
- **Algorithm**: Polygon filling with screen bounds as edges

### Domain Model

#### `GraphBounds`
- **Purpose**: Manages coordinate system
- **Responsibilities**:
  - Stores min/max X/Y coordinates
  - Converts between graph and screen space
  - Provides zoom and pan operations
  - Enforces square aspect ratio
- **Key Methods**:
  - `xToScreen()`, `yToScreen()` - Graph to screen
  - `screenToX()`, `screenToY()` - Screen to graph
  - `zoom()` - Zoom around focal point
  - `pan()` - Translate view
- **Invariant**: Maintains 1:1 aspect ratio (1 unit X = 1 unit Y on screen)

#### `ViewportManager`
- **Purpose**: Handles viewport interactions
- **Responsibilities**:
  - Manages zoom operations (preserves focal point)
  - Manages pan operations (tracks mouse)
  - Preserves zoom level on window resize
- **State**: Tracks drag state and last mouse position
- **Integration**: Works with `GraphBounds` for transformations

#### `Parameter`
- **Purpose**: Represents adjustable parameter
- **Responsibilities**:
  - Stores parameter range and current value
  - Supports continuous and discrete (integer-only) modes
  - Clamps values to valid range
- **Types**:
  - Continuous: `[0:10]` - Any decimal value
  - Discrete: `[1;5]` - Only integers 1, 2, 3, 4, 5
- **UI**: Bound to slider component for interactive control

### User Interface

#### `GraphingCalculator`
- **Purpose**: Main application window
- **Responsibilities**:
  - Sets up application frame
  - Creates and positions panels
  - Initializes components
- **Layout**: Split pane with `FunctionPanel` (left) and `GraphPanel` (right)

#### `FunctionPanel`
- **Purpose**: Manages function list UI
- **Responsibilities**:
  - Displays all function/parameter entries
  - Handles adding/removing entries
  - Parses entries and updates graph
  - Manages function/set separation
- **Components**: List of `FunctionEntry` and `ParameterEntry` widgets
- **Integration**: Calls `GraphPanel.setFunctions()` with parsed results

#### `GraphPanel`
- **Purpose**: Displays the graph
- **Responsibilities**:
  - Renders graph using `GraphRenderer`
  - Handles mouse interactions (zoom, pan)
  - Manages viewport and bounds
  - Triggers repaints on changes
- **Event Handling**:
  - Mouse wheel → zoom
  - Mouse drag → pan
  - Window resize → preserve zoom
- **Pattern**: View component with delegation to renderer

#### `FunctionEntry`
- **Purpose**: UI component for single function
- **Responsibilities**:
  - Displays color indicator, text field, controls
  - Handles enable/disable checkbox
  - Manages edit/delete actions
  - Only shows controls for plottable functions
- **Dynamic Behavior**: Recreates controls when function type changes (e.g., set → expression)

#### `ParameterEntry`
- **Purpose**: UI component for parameter with slider
- **Responsibilities**:
  - Displays parameter name and range
  - Provides slider for adjusting value
  - Updates graph on value change
- **Integration**: Notifies `FunctionPanel` on parameter change to trigger graph update

## Design Patterns Used

- **Template Method**: `BaseFunction` / `PlottableFunction` point computation
- **Factory Method**: `FunctionFactory` creates appropriate function types
- **Strategy**: Different function types use different point computation strategies
- **Observer**: UI components notify panel on changes
- **Coordinator**: `GraphRenderer` orchestrates rendering pipeline
- **Caching**: `PlottableFunction` caches computed points

## Technical Details

- **Language**: Java 8+
- **Dependencies**: JLaTeXMath (included as `lib/jlatexmath.jar`)
- **Architecture**: Layered (UI → Rendering → Model → Core)
- **Rendering**: Java2D (Graphics2D)
- **Algorithms**: 
  - Adaptive sampling for smooth curves
  - Bisection method for intersections
  - Numerical differentiation for slope detection

## Documentation

- **[PACKAGE_STRUCTURE.md](PACKAGE_STRUCTURE.md)** - Detailed package documentation
- **[REFACTORING_SUMMARY.md](REFACTORING_SUMMARY.md)** - Recent refactoring overview

## License

Open source. Free to use and modify.

## Contributing

Contributions welcome! Feel free to submit pull requests.






