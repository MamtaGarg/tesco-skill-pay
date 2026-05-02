package com.tesco.skillpay.service;

import com.tesco.skillpay.model.Employee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Orchestrates the weekly pay calculation for one or many employees,
 * including overtime pay as a third component alongside contract pay
 * and skill pay.
 *
 * <p>This is the overtime-aware counterpart of {@link WeeklyPayCalculator}.
 * The original calculator is left untouched for phase-1 consumers. This
 * class delegates to three {@link PayComponentCalculator} instances:</p>
 * <ol>
 *   <li>{@link ContractPayCalculator} — base contract pay</li>
 *   <li>{@link SkillPayCalculator} — skill differential pay</li>
 *   <li>{@link OvertimePayCalculator} — overtime premium pay</li>
 * </ol>
 *
 * <p>Results are returned as {@link OvertimePayBreakdown} which exposes
 * each component individually plus the grand total.</p>
 */
public final class OvertimeWeeklyPayCalculator {

    private final PayComponentCalculator contractPayCalculator;
    private final PayComponentCalculator skillPayCalculator;
    private final PayComponentCalculator overtimePayCalculator;

    /**
     * Creates an overtime-aware calculator with all default component calculators
     * (default overtime threshold = 40 hours, multiplier = 0.5×).
     */
    public OvertimeWeeklyPayCalculator() {
        this(new ContractPayCalculator(), new SkillPayCalculator(), new OvertimePayCalculator());
    }

    /**
     * Creates an overtime-aware calculator with custom component calculators
     * for full dependency-injection support.
     */
    public OvertimeWeeklyPayCalculator(PayComponentCalculator contractPayCalculator,
                                       PayComponentCalculator skillPayCalculator,
                                       PayComponentCalculator overtimePayCalculator) {
        this.contractPayCalculator = Objects.requireNonNull(contractPayCalculator,
                "contractPayCalculator must not be null");
        this.skillPayCalculator = Objects.requireNonNull(skillPayCalculator,
                "skillPayCalculator must not be null");
        this.overtimePayCalculator = Objects.requireNonNull(overtimePayCalculator,
                "overtimePayCalculator must not be null");
    }

    /**
     * Calculate the weekly pay breakdown (with overtime) for a single employee.
     */
    public OvertimePayBreakdown calculate(Employee employee) {
        Objects.requireNonNull(employee, "employee must not be null");

        BigDecimal contractedPay = contractPayCalculator.calculate(employee);
        BigDecimal skillBasedPay = skillPayCalculator.calculate(employee);
        BigDecimal overtimePay = overtimePayCalculator.calculate(employee);

        return new OvertimePayBreakdown(employee.getEmployeeId(), contractedPay, skillBasedPay, overtimePay);
    }

    /**
     * Calculate the weekly pay breakdowns (with overtime) for a list of employees,
     * preserving input order.
     */
    public List<OvertimePayBreakdown> calculate(List<Employee> employees) {
        Objects.requireNonNull(employees, "employees must not be null");
        return employees.stream()
                .map(this::calculate)
                .collect(Collectors.toList());
    }
}
