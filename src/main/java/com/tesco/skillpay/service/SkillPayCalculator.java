package com.tesco.skillpay.service;

import com.tesco.skillpay.model.Employee;
import com.tesco.skillpay.model.Shift;

import java.math.BigDecimal;

/**
 * Calculates the skill-based differential pay for an employee for the week.
 *
 * <p>For each shift, the skill-based pay is computed as the working hours
 * (total minus break) multiplied by the hourly rate of the skill performed
 * during that shift. The contributions across all shifts are then summed.</p>
 *
 * <p>This iterates each shift exactly once — O(n) where n is the number
 * of shifts — so the calculation stays efficient even for colleagues
 * with many shifts.</p>
 */
public final class SkillPayCalculator implements PayComponentCalculator {

    @Override
    public BigDecimal calculate(Employee employee) {
        return employee.getShifts().stream()
                .map(this::payForShift)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal payForShift(Shift shift) {
        return shift.getSkill().getHourlyRate().multiply(shift.getWorkingHours());
    }
}
