package com.tesco.skillpay;

import com.tesco.skillpay.model.ContractType;
import com.tesco.skillpay.model.Employee;
import com.tesco.skillpay.model.Shift;
import com.tesco.skillpay.model.Skill;
import com.tesco.skillpay.service.PayBreakdown;
import com.tesco.skillpay.service.WeeklyPayCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Entry point demonstrating the Skill-Based Pay calculation system.
 *
 * <p>The sample data below reproduces the example from the brief:
 * <pre>
 *   Employee-1 -> pay = 250.5
 *   Employee-2 -> pay = 140
 *   Employee-3 -> pay = 173.5
 * </pre>
 * </p>
 */
public final class TescoSkillBasedPayMain {

    private TescoSkillBasedPayMain() {
        // utility entry-point class
    }

    public static void main(String[] args) {
        List<Employee> employees = buildSampleEmployees();

        WeeklyPayCalculator calculator = new WeeklyPayCalculator();
        List<PayBreakdown> breakdowns = calculator.calculate(employees);

        System.out.println("=== Tesco Weekly Pay Report ===");
        breakdowns.forEach(TescoSkillBasedPayMain::printBreakdown);
    }

    private static void printBreakdown(PayBreakdown breakdown) {
        System.out.printf(
                "%s -> pay = %s   (contracted = %s, skill-based = %s)%n",
                breakdown.getEmployeeId(),
                format(breakdown.getTotalPay()),
                format(breakdown.getContractedPay()),
                format(breakdown.getSkillBasedPay()));
    }

    private static String format(BigDecimal amount) {
        // Strip trailing zeros so 140.00 prints as 140, while 250.50 prints as 250.5.
        return amount.setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * Builds the three sample employees from the brief. Shifts are defined
     * with both total duration and break duration so the calculator must
     * derive paid working hours.
     */
    private static List<Employee> buildSampleEmployees() {

        // Employee-1: Permanent, 18 paid working hours.
        //   6h bakery + 5h customer service + 7h cleaning
        //   Contracted = 12.5 * 18 = 225.00
        //   Skill      = 6*2 + 5*1.3 + 7*1 = 25.5
        //   Total      = 250.50
        Employee employee1 = new Employee(
                "Employee-1",
                ContractType.PERMANENT,
                List.of(
                        Shift.of("S1-1", Skill.BAKERY, 7.0, 1.0),
                        Shift.of("S1-2", Skill.CUSTOMER_SERVICE, 6.0, 1.0),
                        Shift.of("S1-3", Skill.CLEANING, 8.0, 1.0)
                ));

        // Employee-2: Permanent, 10 paid working hours.
        //   5h security + 5h bakery
        //   Contracted = 12.5 * 10 = 125.00
        //   Skill      = 5*1 + 5*2 = 15
        //   Total      = 140.00
        Employee employee2 = new Employee(
                "Employee-2",
                ContractType.PERMANENT,
                List.of(
                        Shift.of("S2-1", Skill.SECURITY, 5.0, 0.0),
                        Shift.of("S2-2", Skill.BAKERY, 5.0, 0.0)
                ));

        // Employee-3: Permanent, 12 paid working hours.
        //   5h shift leader + 2h cleaning + 5h customer service
        //   Contracted = 12.5 * 12 = 150.00
        //   Skill      = 5*3 + 2*1 + 5*1.3 = 23.5
        //   Total      = 173.50
        Employee employee3 = new Employee(
                "Employee-3",
                ContractType.PERMANENT,
                List.of(
                        Shift.of("S3-1", Skill.SHIFT_LEADER, 6.0, 1.0),
                        Shift.of("S3-2", Skill.CLEANING, 2.5, 0.5),
                        Shift.of("S3-3", Skill.CUSTOMER_SERVICE, 5.5, 0.5)
                ));

        return List.of(employee1, employee2, employee3);
    }
}
