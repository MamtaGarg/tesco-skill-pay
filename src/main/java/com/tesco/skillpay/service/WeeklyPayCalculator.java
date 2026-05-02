package com.tesco.skillpay.service;

import com.tesco.skillpay.model.Employee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Orchestrates the weekly pay calculation for one or many employees by
 * delegating to the configured component calculators (contract pay,
 * skill pay, etc.).
 *
 * <p>Phase 1 wires up {@link ContractPayCalculator} and
 * {@link SkillPayCalculator}. The future overtime enhancement will fit
 * naturally as either an extra {@link PayComponentCalculator} or a
 * decorated skill-pay strategy.</p>
 */
public final class WeeklyPayCalculator {

    private final PayComponentCalculator contractPayCalculator;
    private final PayComponentCalculator skillPayCalculator;

    public WeeklyPayCalculator() {
        this(new ContractPayCalculator(), new SkillPayCalculator());
    }

    public WeeklyPayCalculator(PayComponentCalculator contractPayCalculator,
                               PayComponentCalculator skillPayCalculator) {
        this.contractPayCalculator = Objects.requireNonNull(contractPayCalculator,
                "contractPayCalculator must not be null");
        this.skillPayCalculator = Objects.requireNonNull(skillPayCalculator,
                "skillPayCalculator must not be null");
    }

    /**
     * Calculate the weekly pay breakdown for a single employee.
     */
    public PayBreakdown calculate(Employee employee) {
        Objects.requireNonNull(employee, "employee must not be null");

        BigDecimal contractedPay = contractPayCalculator.calculate(employee);
        BigDecimal skillBasedPay = skillPayCalculator.calculate(employee);

        return new PayBreakdown(employee.getEmployeeId(), contractedPay, skillBasedPay);
    }

    /**
     * Calculate the weekly pay breakdowns for a list of employees,
     * preserving input order.
     */
    public List<PayBreakdown> calculate(List<Employee> employees) {
        Objects.requireNonNull(employees, "employees must not be null");
        return employees.stream()
                .map(this::calculate)
                .collect(Collectors.toList());
    }
}
