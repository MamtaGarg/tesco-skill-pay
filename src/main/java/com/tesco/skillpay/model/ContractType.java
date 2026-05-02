package com.tesco.skillpay.model;

import java.math.BigDecimal;

/**
 * Contract types Tesco offers to its colleagues, paired with the base
 * hourly contract pay rate (in £).
 */
public enum ContractType {

    PERMANENT("Permanent", new BigDecimal("12.5")),
    AGENCY("Agency", new BigDecimal("13.0")),
    ZERO_HOURS("Zero-hours", new BigDecimal("13.0"));

    private final String displayName;
    private final BigDecimal hourlyRate;

    ContractType(String displayName, BigDecimal hourlyRate) {
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
