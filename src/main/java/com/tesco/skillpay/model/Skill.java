package com.tesco.skillpay.model;

import java.math.BigDecimal;

/**
 * Skill catalogue with the differential hourly pay rate (in £) per skill.
 *
 * <p>Rates are stored as {@link BigDecimal} to avoid floating-point precision
 * errors during monetary calculations.</p>
 */
public enum Skill {

    BAKERY("Bakery", new BigDecimal("2.0")),
    CHECKOUT_CASHIER("Checkout Cashier", new BigDecimal("1.2")),
    CUSTOMER_SERVICE("Customer Service", new BigDecimal("1.3")),
    SHIFT_LEADER("Shift Leader", new BigDecimal("3.0")),
    SECURITY("Security", new BigDecimal("1.0")),
    CLEANING("Cleaning", new BigDecimal("1.0")),
    DELIVERY_DRIVER("Delivery Driver", new BigDecimal("2.0"));

    private final String displayName;
    private final BigDecimal hourlyRate;

    Skill(String displayName, BigDecimal hourlyRate) {
        this.displayName = displayName;
        this.hourlyRate = hourlyRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }
}
