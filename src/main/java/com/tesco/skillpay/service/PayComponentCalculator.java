package com.tesco.skillpay.service;

import com.tesco.skillpay.model.Employee;

import java.math.BigDecimal;

/**
 * Strategy interface for calculating an individual component of an
 * employee's weekly pay (contracted pay, skill-based pay, future
 * overtime, holiday pay, etc.).
 *
 * <p>By keeping each component behind a small interface, new components
 * can be plugged into {@link WeeklyPayCalculator} without modifying the
 * existing ones — the open/closed principle applied to payroll.</p>
 */
@FunctionalInterface
public interface PayComponentCalculator {

    /**
     * @param employee the colleague whose pay component is being computed
     * @return the monetary value (in £) of this pay component for the week
     */
    BigDecimal calculate(Employee employee);
}
