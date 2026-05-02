package com.tesco.skillpay.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a single shift worked by a colleague.
 *
 * <p>The actual working hours used for pay calculation are derived as
 * {@code totalDurationHours - breakDurationHours}, since paid breaks are
 * outside the scope of phase 1. Both durations are stored in hours
 * to keep arithmetic simple and unambiguous.</p>
 */
public final class Shift {

    private final String shiftId;
    private final Skill skill;
    private final BigDecimal totalDurationHours;
    private final BigDecimal breakDurationHours;

    public Shift(String shiftId,
                 Skill skill,
                 BigDecimal totalDurationHours,
                 BigDecimal breakDurationHours) {
        this.shiftId = Objects.requireNonNull(shiftId, "shiftId must not be null");
        this.skill = Objects.requireNonNull(skill, "skill must not be null");
        this.totalDurationHours = Objects.requireNonNull(totalDurationHours, "totalDurationHours must not be null");
        this.breakDurationHours = Objects.requireNonNull(breakDurationHours, "breakDurationHours must not be null");

        if (totalDurationHours.signum() < 0) {
            throw new IllegalArgumentException("totalDurationHours must be non-negative");
        }
        if (breakDurationHours.signum() < 0) {
            throw new IllegalArgumentException("breakDurationHours must be non-negative");
        }
        if (breakDurationHours.compareTo(totalDurationHours) > 0) {
            throw new IllegalArgumentException(
                    "breakDurationHours (" + breakDurationHours +
                            ") cannot exceed totalDurationHours (" + totalDurationHours + ")");
        }
    }

    /**
     * Convenience constructor that accepts hours as primitive doubles.
     */
    public static Shift of(String shiftId, Skill skill, double totalDurationHours, double breakDurationHours) {
        return new Shift(
                shiftId,
                skill,
                BigDecimal.valueOf(totalDurationHours),
                BigDecimal.valueOf(breakDurationHours));
    }

    public String getShiftId() {
        return shiftId;
    }

    public Skill getSkill() {
        return skill;
    }

    public BigDecimal getTotalDurationHours() {
        return totalDurationHours;
    }

    public BigDecimal getBreakDurationHours() {
        return breakDurationHours;
    }

    /**
     * @return the actual paid working hours for this shift (total minus break).
     */
    public BigDecimal getWorkingHours() {
        return totalDurationHours.subtract(breakDurationHours);
    }

    @Override
    public String toString() {
        return "Shift{" +
                "shiftId='" + shiftId + '\'' +
                ", skill=" + skill +
                ", totalDurationHours=" + totalDurationHours +
                ", breakDurationHours=" + breakDurationHours +
                '}';
    }
}
