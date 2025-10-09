# UI Package Refactoring - Function Entry Hierarchy

## Overview

The `lib.ui` package has been refactored to create a cleaner hierarchy of UI entry components that mirrors the domain model's `Function` hierarchy. This improves code organization, maintainability, and makes it easier to add new function types.

---

## New Class Hierarchy

```
AbstractFunctionEntry (abstract)
├── BaseFunctionEntry (non-plottable functions)
│   ├── ConstantFunctionEntry (constants with sliders)
│   └── [Future: other BaseFunction types]
│
└── PlottableFunctionEntry (graphical functions)
    └── [All plottable function types]
```

---

## Class Descriptions

### `AbstractFunctionEntry` (Abstract Base Class)

**Purpose**: Defines the common UI structure for any `Function` object in the left panel.

**Common Components**:
- Expression field (for editing)
- Display label (for viewing)
- Edit button
- Delete button
- Card layout for view/edit mode switching

**Abstract Methods**:
- `layoutSpecificComponents(JPanel topPanel)`: Add function-type-specific UI elements
- `layoutAdditionalComponents()`: Add components beyond standard top/center panels
- `updateFromEdit(String newExpression)`: Handle expression changes
- `getFunction()`: Return the underlying Function model object
- `getMaxHeight()`: Define the entry's maximum height

**Key Features**:
- Template Method pattern for common UI flow
- Subclasses customize specific aspects
- Handles edit/view mode switching automatically

---

### `BaseFunctionEntry` (Non-Plottable Functions)

**Purpose**: UI entry for `BaseFunction` objects (definitions that don't render on graph).

**Examples**: 
- `SetFunction` (e.g., `A={1,2,3}`)
- Simple constants

**UI Components**:
- ✅ Expression display/edit
- ✅ Delete button
- ❌ No color picker (not rendered)
- ❌ No enable/disable checkbox (always active)
- ❌ No slider (unless it's a ConstantFunction)

**Use Case**: Discrete sets, value collections, simple definitions

---

### `ConstantFunctionEntry` (Constants with Sliders)

**Purpose**: Specialized entry for `ConstantFunction` objects with slider ranges.

**Examples**:
- Continuous: `a=[0:10]` (decimal slider)
- Discrete: `n=[1..5]` (integer slider)

**UI Components**:
- ✅ Expression display/edit
- ✅ Delete button
- ✅ **Slider** (interactive value control)
- ✅ **Value label** (current value display)
- ✅ **Range labels** (min/max indicators)
- ❌ No color picker
- ❌ No enable/disable checkbox

**Key Features**:
- Replaces old `ParameterEntry` (now deprecated)
- Slider automatically configured for discrete vs continuous
- Updates graph in real-time as slider moves
- Can be updated externally (e.g., from point dragging)

**Height**: 150px (taller to accommodate slider)

---

### `PlottableFunctionEntry` (Graphical Functions)

**Purpose**: UI entry for `PlottableFunction` objects (rendered on graph).

**Examples**:
- `RegularFunction` (e.g., `f(x)=x^2`)
- `EquationFunction` (e.g., `(x^2=2*x+1)`)
- `InequationFunction` (e.g., `(x^2>=2*x)`)
- `PointFunction` (e.g., `P=(cos(t), sin(t))`)

**UI Components**:
- ✅ Expression display/edit
- ✅ Delete button
- ✅ **Color indicator** (clickable for color picker)
- ✅ **Enable/disable checkbox** (show/hide on graph)

**Key Features**:
- Full visual controls for graph rendering
- Color picker dialog on indicator click
- Toggle visibility without deleting
- Syncs enabled state with Function model

---

## Migration Path

### Old Structure (Deprecated)
```java
FunctionEntry entry = new FunctionEntry(expr, color, this, showVisualControls);
ParameterEntry paramEntry = new ParameterEntry(param, listener);
```

### New Structure (Recommended)
```java
// For plottable functions
PlottableFunctionEntry entry = new PlottableFunctionEntry(expr, plottableFunc, this);

// For constants with sliders
ConstantFunctionEntry constEntry = new ConstantFunctionEntry(expr, constantFunc, this);

// For simple sets/definitions
BaseFunctionEntry baseEntry = new BaseFunctionEntry(expr, baseFunc, this);
```

---

## Benefits of Refactoring

### 1. **Type Safety**
- Each entry type is tied to its corresponding Function model class
- Compiler catches mismatches between UI and model

### 2. **Clarity**
- Entry type clearly indicates what kind of function it represents
- No boolean flags like `showVisualControls` needed

### 3. **Extensibility**
- Easy to add new entry types for new function types
- Just extend appropriate base class

### 4. **Consistency**
- UI hierarchy mirrors domain model hierarchy
- Easier to understand and maintain

### 5. **Separation of Concerns**
- Each entry type handles only its specific UI needs
- Common functionality in abstract base class

---

## Future Enhancements

### Potential New Entry Types
```
AbstractFunctionEntry
├── BaseFunctionEntry
│   ├── ConstantFunctionEntry ✅ (implemented)
│   ├── SetFunctionEntry (future: specialized set UI)
│   └── MatrixFunctionEntry (future: matrix definitions)
│
└── PlottableFunctionEntry
    ├── RegularFunctionEntry (future: function-specific controls)
    ├── EquationFunctionEntry (future: intersection tools)
    └── PointFunctionEntry (future: draggable point controls)
```

### Next Steps
1. Update `FunctionPanel.addFunction()` to instantiate correct entry type based on parsed Function
2. Gradually migrate all references from old `FunctionEntry` to new hierarchy
3. Add factory method to `FunctionPanel` for entry creation
4. Consider removing deprecated `ParameterEntry` entirely

---

## Code Examples

### Creating a Plottable Function Entry
```java
// Parse expression and create function model
PlottableFunction function = factory.createFunction(expression, color);

// Create corresponding UI entry
PlottableFunctionEntry entry = new PlottableFunctionEntry(
    expression, 
    function, 
    functionPanel
);

// Add to panel
entriesPanel.add(entry);
```

### Creating a Constant with Slider
```java
// Create constant function with range
ConstantFunction constant = new ConstantFunction("a", 0.0, 10.0);

// Create UI entry with slider
ConstantFunctionEntry entry = new ConstantFunctionEntry(
    "a=[0:10]",
    constant,
    functionPanel
);

// Entry automatically displays slider
entriesPanel.add(entry);
```

### Creating a Simple Set
```java
// Create set function
SetFunction set = new SetFunction("A", values);

// Create UI entry (no visual controls)
BaseFunctionEntry entry = new BaseFunctionEntry(
    "A={1,2,3}",
    set,
    functionPanel
);

// Entry shows only definition
entriesPanel.add(entry);
```

---

## Testing Checklist

- [ ] PlottableFunctionEntry displays color picker and checkbox
- [ ] BaseFunctionEntry shows only expression and delete button
- [ ] ConstantFunctionEntry displays slider correctly
- [ ] Slider updates function value in real-time
- [ ] Color picker changes function color
- [ ] Enable/disable checkbox toggles visibility
- [ ] Edit mode works for all entry types
- [ ] Delete button removes entry from panel
- [ ] Graph updates when entries change

---

## Notes

- The old `FunctionEntry` class still exists for backward compatibility
- The old `ParameterEntry` class is now deprecated
- `FunctionPanel` has been updated to support both old and new entry types
- Migration to new types can be done gradually
