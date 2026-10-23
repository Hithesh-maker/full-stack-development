# Task 7: Interactive Web Form with Events & Functions

## Overview
An interactive front-end web application demonstrating dynamic DOM manipulation, real-time event-driven programming, and modular JavaScript functions. Features client-side input masking, reactive validation on `input`, `blur`, and `change` events, and accessible UI feedback.

---

## Technical Specifications

- **HTML5**: Semantic form elements (`<fieldset>`, `<legend>`, `<input>`, `<select>`, `<button>`).
- **CSS3**: Mobile-first responsive styling, micro-interactions, CSS custom properties (variables), accessible focus rings.
- **JavaScript**:
  - Event Listeners: `addEventListener('input')`, `addEventListener('change')`, `addEventListener('submit')`.
  - Regular Expressions for complex pattern matching (Postal codes, phone numbers, email validation).
  - Dynamic DOM updates: live character counters, dynamic fee calculation, dependent dropdown options.

---

## Key Features

1. **Reactive Event Listeners**:
   - `input`: Instant character counter and password strength estimator.
   - `blur`: Validation trigger highlighting missing or invalid fields.
   - `change`: Dynamically repopulates dependent course/subject choices based on selected major.
2. **Form Reset & State Preservation**:
   - Programmatic form resetting with confirmation modal and feedback toast.
3. **Async Submission Simulation**:
   - Simulates AJAX submission with loading spinner, disabled submit buttons to prevent double submission, and animated success confirmation.

---

## Project Structure

```text
Task 7 - Interactive Web Form with Events & Functions/
├── index.html       # Interactive form interface
├── style.css        # Modern, responsive UI with state transitions
├── script.js        # Event handlers, validators, and dynamic calculators
└── README.md        # Comprehensive task documentation
```

---

## Execution Guide

1. Open `Task 7 - Interactive Web Form with Events & Functions/index.html` directly in any modern browser.
2. Test interactive behaviors:
   - Type in the password field to see real-time strength indicators.
   - Change the department dropdown to observe dynamic updates in dependent course lists.
   - Submit invalid data to see immediate feedback tooltips.
