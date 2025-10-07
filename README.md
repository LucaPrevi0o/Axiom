# Axiom - Graphing Calculator

A Java-based graphing calculator with support for mathematical functions, user-defined functions, and intersection analysis.

## Features

- **Interactive Graphing**: Plot mathematical functions with real-time visualization
- **User-Defined Functions**: Create named functions and use them in other expressions (e.g., `f(x)=x^2`)
- **Intersection Detection**: Find and display intersection points between functions using `(expr1=expr2)` syntax
- **Zoom and Pan**: Mouse wheel to zoom, click and drag to pan
- **Color-Coded Functions**: Each function has a customizable color
- **LaTeX Rendering**: Beautiful mathematical notation (requires jlatexmath.jar)

## Supported Functions

- Trigonometric: `sin`, `cos`, `tan`
- Logarithmic: `log` (base 10), `ln` (natural log)
- Other: `sqrt`, `abs`
- Constants: `pi`, `e`
- Operators: `+`, `-`, `*`, `/`, `^` (power)

## Usage

### Building and Running

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

### Examples

1. **Simple function**: `x^2 + 2*x - 3`
2. **Named function**: `f(x)=sin(x)`
3. **Using named function**: `2*f(x)` (requires f(x) to be defined first)
4. **Intersection**: `(x^2=2*x+1)` - finds where x² intersects with 2x+1
5. **Named intersection**: `g(x)=(x^2=sin(x))` - creates a named intersection function

## Project Structure

```
Axiom/
├── Axiom.java              # Main entry point
├── lib/
│   ├── expression/         # Expression parsing and evaluation
│   │   ├── ExpressionEvaluator.java
│   │   └── ExpressionParser.java
│   ├── function/          # Function entry UI components
│   │   ├── FunctionEntry.java
│   │   ├── FunctionPanel.java
│   │   └── FunctionParser.java      # NEW: Expression parsing utility
│   ├── graph/             # Graphing components
│   │   ├── GraphFunction.java
│   │   ├── GraphPanel.java          # Refactored: Now a coordinator
│   │   ├── GraphingCalculator.java
│   │   ├── GraphBounds.java         # NEW: Coordinate management
│   │   ├── IntersectionFinder.java  # NEW: Numerical methods
│   │   ├── GraphRenderer.java       # NEW: Rendering logic
│   │   └── ViewportManager.java     # NEW: Viewport operations
│   └── jlatexmath.jar     # LaTeX rendering library (optional)
├── jmake                  # Build script
├── README.md              # This file
├── REFACTORING_SUMMARY.md # Initial refactoring summary
└── ADVANCED_REFACTORING.md # Advanced refactoring details
```

## Architecture

The application follows a **modular, SOLID-based architecture** with clear separation of concerns:

### Core Components:

1. **GraphPanel** (Coordinator)
   - Orchestrates rendering and user interactions
   - Delegates to specialized components

2. **GraphBounds** (Data Model)
   - Manages coordinate system and transformations
   - Converts between graph and screen coordinates

3. **ViewportManager** (Interaction Handler)
   - Handles zoom and pan operations
   - Preserves zoom on window resize

4. **IntersectionFinder** (Algorithm)
   - Finds intersection points using numerical methods
   - Implements bisection algorithm

5. **GraphRenderer** (View)
   - Renders grid, axes, functions, and intersections
   - Handles adaptive sampling for smooth curves

6. **FunctionParser** (Utility)
   - Parses function expressions
   - Detects named functions and intersections

For detailed architecture information, see [ADVANCED_REFACTORING.md](ADVANCED_REFACTORING.md).

## Dependencies

- **Java 8 or higher**: Required to run the application
- **JLaTeXMath** (optional): For rendering mathematical expressions with LaTeX. Included as `lib/jlatexmath.jar`

## Controls

- **Mouse Wheel**: Zoom in/out
- **Click + Drag**: Pan the graph
- **Edit Button**: Modify function expression
- **Color Indicator**: Click to change function color
- **Checkbox**: Enable/disable function plotting
- **× Button**: Delete function
- **+ Add Function**: Add a new function to plot

## Technical Details

### Architecture Overview

```
┌─────────────────────────────────────────────────┐
│         GraphingCalculator (Main Window)        │
└─────────────────────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        ↓                       ↓
┌──────────────┐        ┌──────────────────┐
│ FunctionPanel│        │   GraphPanel     │
│              │───────→│   (Coordinator)  │
└──────────────┘        └──────────────────┘
        │                       │
        ↓                       ↓
┌──────────────┐        ┌──────────────────────────────────┐
│  Function    │        │  Specialized Components:         │
│  Parser      │        │  • GraphBounds (coordinates)     │
└──────────────┘        │  • ViewportManager (zoom/pan)    │
                        │  • IntersectionFinder (math)     │
                        │  • GraphRenderer (drawing)       │
                        └──────────────────────────────────┘
```

### Graph Rendering
- Uses **adaptive sampling** for smooth curves at any zoom level
- Base sampling: 1 sample per screen pixel
- Zoom multiplier: Increases samples when zoomed in (up to 10x)
- Sample range: 50-5000 points per function
- Handles discontinuities gracefully

### Intersection Algorithm
- **Method**: Bisection method for root finding
- **Precision**: BISECTION_EPSILON = 1e-8
- **Max iterations**: 40 per root
- **Sampling**: Adaptive (200-1000 samples based on screen width)
- **Deduplication**: Removes roots closer than 1e-6

### Coordinate System
- **Graph Bounds**: Managed by GraphBounds class
- **Transformations**: Bidirectional (graph ↔ screen)
- **Zoom**: Preserves focal point, maintains aspect on resize
- **Pan**: Translates view with pixel-perfect tracking

## License

This project is open source. Feel free to use and modify as needed.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
