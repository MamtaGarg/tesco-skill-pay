package com.tesco.skillpay;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    enum Skill {
        BAKERY("Bakery", "2.0"),
        CHECKOUT_CASHIER("Checkout Cashier", "1.2"),
        CUSTOMER_SERVICE("Customer Service", "1.3"),
        SHIFT_LEADER("Shift Leader", "3.0"),
        SECURITY("Security", "1.0"),
        CLEANING("Cleaning", "1.0"),
        DELIVERY_DRIVER("Delivery Driver", "2.0");

        private final String label;
        private final BigDecimal differentialRatePerHour;

        Skill(String label, String differentialRatePerHour) {
            this.label = label;
            this.differentialRatePerHour = new BigDecimal(differentialRatePerHour);
        }

        public String label() {
            return label;
        }

        public BigDecimal differentialRatePerHour() {
            return differentialRatePerHour;
        }
    }

    enum ContractType {
        PERMANENT("Permanent", "12.5"),
        AGENCY("Agency", "13.0"),
        ZERO_HOURS("Zero-hours", "13.0");

        private final String label;
        private final BigDecimal contractRatePerHour;

        ContractType(String label, String contractRatePerHour) {
            this.label = label;
            this.contractRatePerHour = new BigDecimal(contractRatePerHour);
        }

        public String label() {
            return label;
        }

        public BigDecimal contractRatePerHour() {
            return contractRatePerHour;
        }
    }

    record Shift(String shiftId, Skill skill, BigDecimal totalDurationHours, BigDecimal breakDurationHours) {
        BigDecimal paidHours() {
            BigDecimal paid = totalDurationHours.subtract(breakDurationHours);
            if (paid.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                    "Invalid shift " + shiftId + ": break duration cannot exceed total duration."
                );
            }
            return paid;
        }
    }

    record Employee(String employeeId, ContractType contractType, List<Shift> shifts) {}

    static final class SkillBasedPayCalculator {

        public BigDecimal calculateWeeklyPay(Employee employee) {
            BigDecimal contractPay = BigDecimal.ZERO;
            BigDecimal skillPay = BigDecimal.ZERO;

            for (Shift shift : employee.shifts()) {
                BigDecimal paidHours = shift.paidHours();
                contractPay = contractPay.add(paidHours.multiply(employee.contractType().contractRatePerHour()));
                skillPay = skillPay.add(paidHours.multiply(shift.skill().differentialRatePerHour()));
            }

            return money(contractPay.add(skillPay));
        }

        public Map<String, BigDecimal> calculateAll(List<Employee> employees) {
            return employees.stream()
                .collect(Collectors.toMap(Employee::employeeId, this::calculateWeeklyPay));
        }

        private BigDecimal money(BigDecimal amount) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }
    }

    private static String formatMoney(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString();
    }

    public static void main(String[] args) {
        // Example data that prints:
        // Employee-1 -> pay = 250.5
        // Employee-2 -> pay = 140
        // Employee-3 -> pay = 173.5
        List<Employee> employees = List.of(
            new Employee(
                "Employee-1",
                ContractType.AGENCY,
                List.of(
                    new Shift("S1", Skill.SHIFT_LEADER, new BigDecimal("12.5"), BigDecimal.ZERO),
                    new Shift("S2", Skill.BAKERY,       new BigDecimal("1.5"),  BigDecimal.ZERO),
                    new Shift("S3", Skill.SECURITY,     new BigDecimal("2.0"),  BigDecimal.ZERO)
                )
            ),
            new Employee(
                "Employee-2",
                ContractType.PERMANENT,
                List.of(
                    new Shift("S4", Skill.SECURITY, new BigDecimal("5.0"), BigDecimal.ZERO),
                    new Shift("S5", Skill.BAKERY,   new BigDecimal("5.0"), BigDecimal.ZERO)
                )
            ),
            new Employee(
                "Employee-3",
                ContractType.ZERO_HOURS,
                List.of(
                    new Shift("S6", Skill.SHIFT_LEADER, new BigDecimal("2.0"), BigDecimal.ZERO),
                    new Shift("S7", Skill.BAKERY,       new BigDecimal("1.5"), BigDecimal.ZERO),
                    new Shift("S8", Skill.CLEANING,     new BigDecimal("8.5"), BigDecimal.ZERO)
                )
            )
        );

        SkillBasedPayCalculator calculator = new SkillBasedPayCalculator();
        Map<String, BigDecimal> result = calculator.calculateAll(employees);

        employees.forEach(e ->
            System.out.println(e.employeeId() + " -> pay = " + formatMoney(result.get(e.employeeId())))
        );
    }
}