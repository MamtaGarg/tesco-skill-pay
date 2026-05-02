package com.tesco.skillpay.service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable result object describing the components of a colleague's
 * weekly pay.
 *
 * <p>Holding contracted pay and skill pay separately (rather than only
 * the total) keeps the breakdown auditable, makes unit testing easier,
 * and leaves room for future components such as overtime pay.</p>
 */
public final class PayBreakdown {

    private final String employeeId;
    private final BigDecimal contractedPay;
    private final BigDecimal skillBasedPay;

    public PayBreakdown(String employeeId, BigDecimal contractedPay, BigDecimal skillBasedPay) {
        this.employeeId = Objects.requireNonNull(employeeId, "employeeId must not be null");
        this.contractedPay = Objects.requireNonNull(contractedPay, "contractedPay must not be null");
        this.skillBasedPay = Objects.requireNonNull(skillBasedPay, "skillBasedPay must not be null");
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

    public BigDecimal getTotalPay() {
        return contractedPay.add(skillBasedPay);
    }

    @Override
    public String toString() {
        return "PayBreakdown{" +
                "employeeId='" + employeeId + '\'' +
                ", contractedPay=" + contractedPay +
                ", skillBasedPay=" + skillBasedPay +
                ", totalPay=" + getTotalPay() +
                '}';
    }
}
