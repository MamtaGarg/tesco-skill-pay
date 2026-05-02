package com.tesco.skillpay.service;

import com.tesco.skillpay.model.Employee;
import com.tesco.skillpay.model.Shift;

import java.math.BigDecimal;

/**
 * Calculates the contracted (base) pay for an employee for the week.
 *
 * <p>Contract pay = total working hours across all shifts × contract hourly rate.</p>
 *
 * <p>Working hours per shift exclude break time, on the assumption that
 * breaks are unpaid. This is configurable in a single place if Tesco
 * later decides paid breaks are in scope.</p>
 */
public final class ContractPayCalculator implements PayComponentCalculator {

    @Override
    public BigDecimal calculate(Employee employee) {
        BigDecimal hourlyRate = employee.getContractType().getHourlyRate();

        BigDecimal totalWorkingHours = employee.getShifts().stream()
                .map(Shift::getWorkingHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return hourlyRate.multiply(totalWorkingHours);
    }
}
