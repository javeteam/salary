package com.aspect.salary.entity;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class Absence {

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy (HH:mm");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm)");

    private LocalDateTime creationDate;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private int bitrixUserId = -1;
    private String absenceType;
    private float durationHours = 0.0f;
    private int durationDays = 0;
    private Weight weight = Weight.NEUTRAL;

    public enum Weight {
        POSITIVE,
        NEUTRAL,
        NEGATIVE
    }

    public Absence(@NotNull java.sql.Timestamp from, @NotNull java.sql.Timestamp to, @NotNull java.sql.Timestamp creationDate) {
        this.dateFrom = from.toLocalDateTime();
        this.dateTo = to.toLocalDateTime();
        this.creationDate = creationDate.toLocalDateTime();
    }

    public Absence(@NotNull LocalDateTime from, @NotNull LocalDateTime to, @NotNull LocalDateTime creationDate, int bitrixUserId, String absenceType) {
        this.dateFrom = from;
        this.dateTo = to;
        this.creationDate = creationDate;
        this.bitrixUserId = bitrixUserId;
        this.absenceType = absenceType;
    }


    public void setBitrixUserId(int bitrixUserId) {
        this.bitrixUserId = bitrixUserId;
    }

    public void setAbsenceType(String absenceType) {
        this.absenceType = absenceType;
        if (absenceType.equals("LEAVESICK") || absenceType.equals("VACATION")) {
            dateFrom = dateFrom.toLocalDate().atTime(LocalTime.MIN);
            dateTo = dateTo.toLocalDate().atTime(LocalTime.MAX);
        }
    }

    public LocalDateTime getDateFrom() {
        return dateFrom;
    }

    public LocalDateTime getDateTo() {
        return dateTo;
    }

    public String getDatesAsString() {
        if (absenceType.equals("LEAVESICK") || absenceType.equals("VACATION")) {
            return dateFormatter.format(dateFrom) + " - " + dateFormatter.format(dateTo);
        } else return dateTimeFormatter.format(dateFrom) + " - " + timeFormatter.format(dateTo);
    }

    public String getAbsenceType() {
        return this.absenceType;
    }

    public int getBitrixUserId() {
        return bitrixUserId;
    }

    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public void setDurationHours(float durationHours) {
        this.durationHours = durationHours;
    }

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    public float getDuration() {
        if (durationDays == 0) return durationHours;
        else return (float) durationDays;
    }

    public int getCoefficient() {
        switch (this.weight) {
            case POSITIVE:
                return 1;
            case NEGATIVE:
                return -1;
            default:
                return 0;
        }
    }


}