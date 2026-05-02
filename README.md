# Tesco Skill-Based Pay Calculator

This project implements a phase-1 Tesco skill-based weekly pay system.

The system calculates:

1. Contracted pay (based on contract type and paid hours)
2. Skill differential pay (based on skill worked and paid hours)
3. Total weekly pay per employee

Phase-1 scope in this codebase:

- Pay is based on contract + skill differential only.
- Paid hours are computed as total shift duration minus break duration.
- Overtime is intentionally not included yet and is left as a future enhancement.

## Why This Version Matters

This repository includes a layered implementation using domain models and service calculators.

The interview-impact entry point is:

- `src/main/java/com/tesco/skillpay/TescoSkillBasedPayMain.java`

This class demonstrates usage of the full architecture (model + service layers), which is stronger than a single-file implementation for maintainability, extensibility, and testing.

## Existing File Structure

- `src/main/java/com/tesco/skillpay/TescoSkillBasedPayMain.java` - Demo entry point using the layered design.
- `src/main/java/com/tesco/skillpay/model/ContractType.java` - Contract types and base hourly rates.
- `src/main/java/com/tesco/skillpay/model/Skill.java` - Skill catalogue and differential rates.
- `src/main/java/com/tesco/skillpay/model/Shift.java` - Shift data and paid-hour validation/derivation.
- `src/main/java/com/tesco/skillpay/model/Employee.java` - Employee aggregate (id, contract type, shifts).
- `src/main/java/com/tesco/skillpay/service/PayComponentCalculator.java` - Strategy interface for pay components.
- `src/main/java/com/tesco/skillpay/service/ContractPayCalculator.java` - Contract pay calculator.
- `src/main/java/com/tesco/skillpay/service/SkillPayCalculator.java` - Skill pay calculator.
- `src/main/java/com/tesco/skillpay/service/PayBreakdown.java` - Immutable output (contract, skill, total).
- `src/main/java/com/tesco/skillpay/service/WeeklyPayCalculator.java` - Orchestrator for one or many employees.
- `src/test/java/com/tesco/skillpay/service/WeeklyPayCalculatorTest.java` - Unit tests for examples, edge cases, and validations.

## Business Rules Implemented

### Contract Pay Rates

- Permanent: 12.5/hour
- Agency: 13.0/hour
- Zero-hours: 13.0/hour

### Skill Differential Rates

- Bakery: 2.0/hour
- Checkout Cashier: 1.2/hour
- Customer Service: 1.3/hour
- Shift Leader: 3.0/hour
- Security: 1.0/hour
- Cleaning: 1.0/hour
- Delivery Driver: 2.0/hour

### Shift Hours Rule

For each shift:

paidHours = totalDurationHours - breakDurationHours

Validation in `Shift` prevents invalid inputs:

- total duration cannot be negative
- break duration cannot be negative
- break duration cannot exceed total duration

## Calculation Formula

For each employee over all shifts in the week:

1. Contracted pay:

contractedPay = sum(paidHours) * contractRate

2. Skill-based pay:

skillBasedPay = sum(paidHours(shift) * skillRate(shift))

3. Total pay:

totalPay = contractedPay + skillBasedPay

Monetary arithmetic uses `BigDecimal` to avoid floating-point precision issues.

## Example Output (Current Demo)

Running the demo entry point prints:

- Employee-1 -> pay = 250.5
- Employee-2 -> pay = 140
- Employee-3 -> pay = 173.5

This matches the expected phase-1 sample outcomes.

## How To Run

From project root:

```bash
mvn test
mvn -q exec:java -Dexec.mainClass=com.tesco.skillpay.TescoSkillBasedPayMain
```

Alternative demo class also exists:

```bash
mvn -q exec:java -Dexec.mainClass=com.tesco.skillpay.Main
```

## Test Coverage Summary

`WeeklyPayCalculatorTest` covers:

- brief example scenario
- Employee-1 / Employee-2 / Employee-3 scenarios
- contract type rate behavior (agency, zero-hours)
- unpaid-break handling
- no-shift employee (zero pay)
- batch processing order
- invalid shift validation
- null input handling

## TescoSkillBasedPayMain vs One-File Main

### TescoSkillBasedPayMain (recommended)

Strengths:

- clear separation of concerns
- easy to add overtime as another pay component
- highly testable design
- reusable service layer
- better long-term maintainability

### Main (single-file style)

Strengths:

- fast to write in limited time
- compact for quick demonstrations

Tradeoff:

- less modular and less scalable for new policy rules

## Interview Positioning

If asked which approach to choose:

1. Use the layered TescoSkillBasedPayMain architecture as the primary implementation.
2. Mention that the one-file Main approach is a quick fallback for short coding rounds.
3. Emphasize that both are efficient over shifts, but layered design wins for extensibility, testing, and team maintenance.

## Future Enhancement Path (Overtime)

This design is ready for overtime support.

Two clean extension options:

1. Add `OvertimePayCalculator` implementing `PayComponentCalculator` and compose it in `WeeklyPayCalculator`.
2. Decorate or extend existing skill/contract calculators with overtime thresholds and rates.

Either option avoids rewriting existing business logic.

## Notes

- The project currently uses Java and Maven.
- Output formatting strips unnecessary trailing zeros (for example, 140.00 prints as 140).
