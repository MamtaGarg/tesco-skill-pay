package com.tesco.skillpay.service;

import com.tesco.skillpay.model.Employee;
import com.tesco.skillpay.model.Shift;

import java.math.BigDecimal;

/**
 * Calculates the overtime premium pay for an employee for the week.
 *
 * <p>Overtime applies when total paid working hours across all shifts in a
 * week exceed a configurable threshold (default 40 hours). Hours beyond
 * the threshold are paid an additional premium on top of the contract rate.</p>
 *
 * <p>Since {@link ContractPayCalculator} already pays ALL hours at the base
 * contract rate, this calculator only adds the <em>premium</em> portion.
 * For example, at the default 0.5× multiplier the employee effectively
 * earns 1.5× their contract rate for each overtime hour.</p>
 *
 * <h3>Formula</h3>
 * <pre>
 *   overtimeHours   = max(0, totalPaidHours − weeklyThreshold)
 *   overtimePremium = overtimeHours × contractRate × overtimeMultiplier
 * </pre>
 */
public final class OvertimePayCalculator implements PayComponentCalculator {

    /** Standard UK full-time weekly threshold. */
    private static final BigDecimal DEFAULT_WEEKLY_THRESHOLD = new BigDecimal("40");

    /** Default premium multiplier (0.5 means time-and-a-half when added to base). */
    private static final BigDecimal DEFAULT_OVERTIME_MULTIPLIER = new BigDecimal("0.5");

    private final BigDecimal weeklyThreshold;
    private final BigDecimal overtimeMultiplier;

    /**
     * Creates an overtime calculator with default threshold (40 hours) and
     * default multiplier (0.5×).
     */
    public OvertimePayCalculator() {
        this(DEFAULT_WEEKLY_THRESHOLD, DEFAULT_OVERTIME_MULTIPLIER);
    }

    /**
     * Creates an overtime calculator with custom threshold and multiplier.
     *
     * @param weeklyThreshold   hours per week before overtime kicks in
     * @param overtimeMultiplier the premium multiplier applied to the contract rate
     */
    public OvertimePayCalculator(BigDecimal weeklyThreshold, BigDecimal overtimeMultiplier) {
        if (weeklyThreshold == null || weeklyThreshold.signum() < 0) {
            throw new IllegalArgumentException("weeklyThreshold must be non-negative and not null");
        }
        if (overtimeMultiplier == null || overtimeMultiplier.signum() < 0) {
            throw new IllegalArgumentException("overtimeMultiplier must be non-negative and not null");
        }
        this.weeklyThreshold = weeklyThreshold;
        this.overtimeMultiplier = overtimeMultiplier;
    }

    @Override
    public BigDecimal calculate(Employee employee) {
        BigDecimal totalPaidHours = employee.getShifts().stream()
                .map(Shift::getWorkingHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overtimeHours = totalPaidHours.subtract(weeklyThreshold).max(BigDecimal.ZERO);

        BigDecimal contractRate = employee.getContractType().getHourlyRate();

        return overtimeHours.multiply(contractRate).multiply(overtimeMultiplier);
    }

    public BigDecimal getWeeklyThreshold() {
        return weeklyThreshold;
    }

    public BigDecimal getOvertimeMultiplier() {
        return overtimeMultiplier;
    }
}
