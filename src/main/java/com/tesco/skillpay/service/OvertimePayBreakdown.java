package com.tesco.skillpay.service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable result object describing the components of a colleague's
 * weekly pay including overtime.
 *
 * <p>This extends the breakdown concept from {@link PayBreakdown} by adding
 * an overtime pay component. It is kept as a separate class so that the
 * original {@link PayBreakdown} remains untouched for phase-1 consumers.</p>
 *
 * <p>Total pay = contracted pay + skill-based pay + overtime pay.</p>
 */
public final class OvertimePayBreakdown {

    private final String employeeId;
    private final BigDecimal contractedPay;
    private final BigDecimal skillBasedPay;
    private final BigDecimal overtimePay;

    public OvertimePayBreakdown(String employeeId,
                                BigDecimal contractedPay,
                                BigDecimal skillBasedPay,
                                BigDecimal overtimePay) {
        this.employeeId = Objects.requireNonNull(employeeId, "employeeId must not be null");
        this.contractedPay = Objects.requireNonNull(contractedPay, "contractedPay must not be null");
        this.skillBasedPay = Objects.requireNonNull(skillBasedPay, "skillBasedPay must not be null");
        this.overtimePay = Objects.requireNonNull(overtimePay, "overtimePay must not be null");
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public BigDecimal getContractedPay() {
        return contractedPay;
    }

    public BigDecimal getSkillBasedPay() {
        return skillBasedPay;
    }

    public BigDecimal getOvertimePay() {
        return overtimePay;
    }

    public BigDecimal getTotalPay() {
        return contractedPay.add(skillBasedPay).add(overtimePay);
    }

    @Override
    public String toString() {
        return "OvertimePayBreakdown{" +
                "employeeId='" + employeeId + '\'' +
                ", contractedPay=" + contractedPay +
                ", skillBasedPay=" + skillBasedPay +
                ", overtimePay=" + overtimePay +
                ", totalPay=" + getTotalPay() +
                '}';
    }
}
