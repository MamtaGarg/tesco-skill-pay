package com.tesco.skillpay.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Tesco colleague along with their contract type and the
 * shifts they have worked during the week.
 *
 * <p>The list of shifts is defensively copied to keep the {@link Employee}
 * value immutable from the caller's perspective.</p>
 */
public final class Employee {

    private final String employeeId;
    private final ContractType contractType;
    private final List<Shift> shifts;

    public Employee(String employeeId, ContractType contractType, List<Shift> shifts) {
        this.employeeId = Objects.requireNonNull(employeeId, "employeeId must not be null");
        this.contractType = Objects.requireNonNull(contractType, "contractType must not be null");
        Objects.requireNonNull(shifts, "shifts must not be null");
        this.shifts = Collections.unmodifiableList(new ArrayList<>(shifts));
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", contractType=" + contractType +
                ", shifts=" + shifts +
                '}';
    }
}
