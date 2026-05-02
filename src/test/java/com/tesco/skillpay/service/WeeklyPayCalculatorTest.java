package com.tesco.skillpay.service;

import com.tesco.skillpay.model.ContractType;
import com.tesco.skillpay.model.Employee;
import com.tesco.skillpay.model.Shift;
import com.tesco.skillpay.model.Skill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeeklyPayCalculatorTest {

    private final WeeklyPayCalculator calculator = new WeeklyPayCalculator();

    @Test
    @DisplayName("Brief example: 5h security + 5h bakery, permanent => £140")
    void exampleFromBrief() {
        Employee employee = new Employee(
                "Employee-2",
                ContractType.PERMANENT,
                List.of(
                        Shift.of("S1", Skill.SECURITY, 5.0, 0.0),
                        Shift.of("S2", Skill.BAKERY, 5.0, 0.0)
                ));

        PayBreakdown breakdown = calculator.calculate(employee);

        assertMoneyEquals("125.0", breakdown.getContractedPay());
        assertMoneyEquals("15.0", breakdown.getSkillBasedPay());
        assertMoneyEquals("140.0", breakdown.getTotalPay());
    }

    @Test
    @DisplayName("Employee-1: 6h bakery + 5h customer service + 7h cleaning, permanent => £250.5")
    void employee1Scenario() {
        Employee employee = new Employee(
                "Employee-1",
                ContractType.PERMANENT,
                List.of(
                        Shift.of("S1", Skill.BAKERY, 7.0, 1.0),
                        Shift.of("S2", Skill.CUSTOMER_SERVICE, 6.0, 1.0),
                        Shift.of("S3", Skill.CLEANING, 8.0, 1.0)
                ));

        PayBreakdown breakdown = calculator.calculate(employee);

        assertMoneyEquals("225.0", breakdown.getContractedPay());
        assertMoneyEquals("25.5", breakdown.getSkillBasedPay());
        assertMoneyEquals("250.5", breakdown.getTotalPay());
    }

    @Test
    @DisplayName("Employee-3: 5h shift leader + 2h cleaning + 5h customer service, permanent => £173.5")
    void employee3Scenario() {
        Employee employee = new Employee(
                "Employee-3",
                ContractType.PERMANENT,
                List.of(
                        Shift.of("S1", Skill.SHIFT_LEADER, 6.0, 1.0),
                        Shift.of("S2", Skill.CLEANING, 2.5, 0.5),
                        Shift.of("S3", Skill.CUSTOMER_SERVICE, 5.5, 0.5)
                ));

        PayBreakdown breakdown = calculator.calculate(employee);

        assertMoneyEquals("150.0", breakdown.getContractedPay());
        assertMoneyEquals("23.5", breakdown.getSkillBasedPay());
        assertMoneyEquals("173.5", breakdown.getTotalPay());
    }

    @Test
    @DisplayName("Agency contract uses £13/hour rate")
    void agencyContractRate() {
        Employee employee = new Employee(
                "Agency-1",
                ContractType.AGENCY,
                List.of(Shift.of("S1", Skill.DELIVERY_DRIVER, 8.0, 0.0)));

        PayBreakdown breakdown = calculator.calculate(employee);

        // 13 * 8 = 104, skill = 2 * 8 = 16
        assertMoneyEquals("104.0", breakdown.getContractedPay());
        assertMoneyEquals("16.0", breakdown.getSkillBasedPay());
        assertMoneyEquals("120.0", breakdown.getTotalPay());
    }

    @Test
    @DisplayName("Zero-hours contract uses £13/hour rate")
    void zeroHoursContractRate() {
        Employee employee = new Employee(
                "Zero-1",
                ContractType.ZERO_HOURS,
                List.of(Shift.of("S1", Skill.CHECKOUT_CASHIER, 4.0, 0.0)));

        PayBreakdown breakdown = calculator.calculate(employee);

        // 13 * 4 = 52, skill = 1.2 * 4 = 4.8
        assertMoneyEquals("52.0", breakdown.getContractedPay());
        assertMoneyEquals("4.8", breakdown.getSkillBasedPay());
        assertMoneyEquals("56.8", breakdown.getTotalPay());
    }

    @Test
    @DisplayName("Break duration is excluded from paid working hours")
    void breaksAreUnpaid() {
        Employee employee = new Employee(
                "Break-1",
                ContractType.PERMANENT,
                List.of(Shift.of("S1", Skill.BAKERY, 9.0, 1.0))); // 8 working hours

        PayBreakdown breakdown = calculator.calculate(employee);

        // Contracted = 12.5 * 8 = 100; Skill = 2 * 8 = 16
        assertMoneyEquals("100.0", breakdown.getContractedPay());
        assertMoneyEquals("16.0", breakdown.getSkillBasedPay());
        assertMoneyEquals("116.0", breakdown.getTotalPay());
    }

    @Test
    @DisplayName("Employee with no shifts => zero pay")
    void employeeWithNoShifts() {
        Employee employee = new Employee("Idle-1", ContractType.PERMANENT, List.of());

        PayBreakdown breakdown = calculator.calculate(employee);

        assertMoneyEquals("0", breakdown.getContractedPay());
        assertMoneyEquals("0", breakdown.getSkillBasedPay());
        assertMoneyEquals("0", breakdown.getTotalPay());
    }

    @Test
    @DisplayName("Batch calculation preserves input order")
    void batchCalculationPreservesOrder() {
        List<Employee> employees = List.of(
                new Employee("Employee-1", ContractType.PERMANENT, List.of(
                        Shift.of("S1", Skill.BAKERY, 7.0, 1.0),
                        Shift.of("S2", Skill.CUSTOMER_SERVICE, 6.0, 1.0),
                        Shift.of("S3", Skill.CLEANING, 8.0, 1.0))),
                new Employee("Employee-2", ContractType.PERMANENT, List.of(
                        Shift.of("S4", Skill.SECURITY, 5.0, 0.0),
                        Shift.of("S5", Skill.BAKERY, 5.0, 0.0))),
                new Employee("Employee-3", ContractType.PERMANENT, List.of(
                        Shift.of("S6", Skill.SHIFT_LEADER, 6.0, 1.0),
                        Shift.of("S7", Skill.CLEANING, 2.5, 0.5),
                        Shift.of("S8", Skill.CUSTOMER_SERVICE, 5.5, 0.5))));

        List<PayBreakdown> breakdowns = calculator.calculate(employees);

        assertEquals(3, breakdowns.size());
        assertEquals("Employee-1", breakdowns.get(0).getEmployeeId());
        assertEquals("Employee-2", breakdowns.get(1).getEmployeeId());
        assertEquals("Employee-3", breakdowns.get(2).getEmployeeId());

        assertMoneyEquals("250.5", breakdowns.get(0).getTotalPay());
        assertMoneyEquals("140.0", breakdowns.get(1).getTotalPay());
        assertMoneyEquals("173.5", breakdowns.get(2).getTotalPay());
    }

    @Test
    @DisplayName("Shift rejects break longer than total duration")
    void shiftValidatesBreakBoundary() {
        assertThrows(IllegalArgumentException.class,
                () -> Shift.of("Bad", Skill.BAKERY, 4.0, 5.0));
    }

    @Test
    @DisplayName("Shift rejects negative durations")
    void shiftValidatesNegativeDurations() {
        assertThrows(IllegalArgumentException.class,
                () -> Shift.of("Bad", Skill.BAKERY, -1.0, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> Shift.of("Bad", Skill.BAKERY, 4.0, -1.0));
    }

    @Test
    @DisplayName("Calculator rejects null employee")
    void rejectsNullEmployee() {
        assertThrows(NullPointerException.class, () -> calculator.calculate((Employee) null));
    }

    /**
     * Compare two monetary values by numeric magnitude rather than scale,
     * so {@code "140"} and {@code "140.00"} compare equal.
     */
    private static void assertMoneyEquals(String expected, BigDecimal actual) {
        assertEquals(0,
                new BigDecimal(expected).compareTo(actual),
                () -> "expected " + expected + " but got " + actual);
    }
}
