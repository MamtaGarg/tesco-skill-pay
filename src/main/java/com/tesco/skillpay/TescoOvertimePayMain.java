package com.tesco.skillpay;

import com.tesco.skillpay.model.ContractType;
import com.tesco.skillpay.model.Employee;
import com.tesco.skillpay.model.Shift;
import com.tesco.skillpay.model.Skill;
import com.tesco.skillpay.service.OvertimePayBreakdown;
import com.tesco.skillpay.service.OvertimeWeeklyPayCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Entry point demonstrating the Overtime-aware Skill-Based Pay calculation.
 *
 * <p>This demo builds on the phase-1 architecture by adding overtime.
 * Overtime kicks in when an employee's total paid hours exceed 40 in
 * a week, paying a 0.5× premium on each overtime hour (effectively
 * time-and-a-half when combined with base contract pay).</p>
 *
 * <h3>Sample Scenarios</h3>
 * <pre>
 *   Employee-OT1: 45 paid hours (5 overtime) — Permanent
 *     Contracted = 12.5 × 45 = 562.5
 *     Skill      = 20×2.0 + 15×1.3 + 10×1.0 = 69.5
 *     Overtime   = 5 × 12.5 × 0.5 = 31.25
 *     Total      = 663.25
 *
 *   Employee-OT2: 38 paid hours (no overtime) — Permanent
 *     Contracted = 12.5 × 38 = 475
 *     Skill      = 20×1.2 + 18×3.0 = 78
 *     Overtime   = 0
 *     Total      = 553
 *
 *   Employee-OT3: 50 paid hours (10 overtime) — Agency
 *     Contracted = 13.0 × 50 = 650
 *     Skill      = 25×2.0 + 25×1.0 = 75
 *     Overtime   = 10 × 13.0 × 0.5 = 65
 *     Total      = 790
 * </pre>
 */
public final class TescoOvertimePayMain {

    private TescoOvertimePayMain() {
        // utility entry-point class
    }

    public static void main(String[] args) {
        List<Employee> employees = buildSampleEmployees();

        OvertimeWeeklyPayCalculator calculator = new OvertimeWeeklyPayCalculator();
        List<OvertimePayBreakdown> breakdowns = calculator.calculate(employees);

        System.out.println("=== Tesco Weekly Pay Report (with Overtime) ===");
        breakdowns.forEach(TescoOvertimePayMain::printBreakdown);
    }

    private static void printBreakdown(OvertimePayBreakdown breakdown) {
        System.out.printf(
                "%s -> pay = %s   (contracted = %s, skill-based = %s, overtime = %s)%n",
                breakdown.getEmployeeId(),
                format(breakdown.getTotalPay()),
                format(breakdown.getContractedPay()),
                format(breakdown.getSkillBasedPay()),
                format(breakdown.getOvertimePay()));
    }

    private static String format(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * Builds sample employees that demonstrate overtime scenarios.
     * Overtime threshold = 40 hours, multiplier = 0.5×.
     */
    private static List<Employee> buildSampleEmployees() {

        // Employee-OT1: Permanent, 45 paid working hours (5 overtime).
        //   20h bakery (22h shift - 2h break)
        //   15h customer service (17h shift - 2h break)
        //   10h cleaning (11h shift - 1h break)
        //   Contracted = 12.5 * 45 = 562.5
        //   Skill      = 20*2.0 + 15*1.3 + 10*1.0 = 40 + 19.5 + 10 = 69.5
        //   Overtime   = 5 * 12.5 * 0.5 = 31.25
        //   Total      = 663.25
        Employee employee1 = new Employee(
                "Employee-OT1",
                ContractType.PERMANENT,
                List.of(
                        Shift.of("S1-1", Skill.BAKERY, 22.0, 2.0),
                        Shift.of("S1-2", Skill.CUSTOMER_SERVICE, 17.0, 2.0),
                        Shift.of("S1-3", Skill.CLEANING, 11.0, 1.0)
                ));

        // Employee-OT2: Permanent, 38 paid working hours (no overtime).
        //   20h checkout cashier (21h shift - 1h break)
        //   18h shift leader (19h shift - 1h break)
        //   Contracted = 12.5 * 38 = 475
        //   Skill      = 20*1.2 + 18*3.0 = 24 + 54 = 78
        //   Overtime   = 0
        //   Total      = 553
        Employee employee2 = new Employee(
                "Employee-OT2",
                ContractType.PERMANENT,
                List.of(
                        Shift.of("S2-1", Skill.CHECKOUT_CASHIER, 21.0, 1.0),
                        Shift.of("S2-2", Skill.SHIFT_LEADER, 19.0, 1.0)
                ));

        // Employee-OT3: Agency, 50 paid working hours (10 overtime).
        //   25h delivery driver (27h shift - 2h break)
        //   25h security (26h shift - 1h break)
        //   Contracted = 13.0 * 50 = 650
        //   Skill      = 25*2.0 + 25*1.0 = 50 + 25 = 75
        //   Overtime   = 10 * 13.0 * 0.5 = 65
        //   Total      = 790
        Employee employee3 = new Employee(
                "Employee-OT3",
                ContractType.AGENCY,
                List.of(
                        Shift.of("S3-1", Skill.DELIVERY_DRIVER, 27.0, 2.0),
                        Shift.of("S3-2", Skill.SECURITY, 26.0, 1.0)
                ));

        return List.of(employee1, employee2, employee3);
    }
}
