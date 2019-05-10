package com.aspect.salary.entity;

import static com.aspect.salary.utils.CommonUtils.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Absence {

    private Integer id;
    private LocalDateTime creationDate;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private int bitrixUserId = -1;
    private String absenceType;
    private float durationHours = 0.0f;
    private int durationDays = 0;
    private Weight weight = Weight.NEUTRAL;

    public Absence(java.sql.Timestamp from, java.sql.Timestamp to, java.sql.Timestamp creationDate) {
        this.dateFrom = (from == null ? null : from.toLocalDateTime());
        this.dateTo = (to == null ? null : to.toLocalDateTime());
        this.creationDate = (creationDate == null ? null : creationDate.toLocalDateTime());
    }

    public Absence(LocalDateTime from, LocalDateTime to, LocalDateTime creationDate, int bitrixUserId, String absenceType) {
        this.dateFrom = from;
        this.dateTo = to;
        this.creationDate = creationDate;
        this.bitrixUserId = bitrixUserId;
        this.absenceType = absenceType;
    }

    public Absence (String absenceType, float durationHours, Weight weight){
        this.absenceType = absenceType;
        this.durationHours = durationHours;
        this.weight = weight;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateFrom() {
        return dateFrom;
    }

    public LocalDateTime getDateTo() {
        return dateTo;
    }

    public String getDatesAsString() {
        if(dateFrom == null || dateTo == null) return "-";
        else if (absenceType.equals("LEAVESICK") || absenceType.equals("VACATION")) {
            return dateFormatter.format(dateFrom) + " - " + dateFormatter.format(dateTo);
        } else return dateTimeFormatter.format(dateFrom) + " - " + timeFormatter.format(dateTo);
    }

    public String getAbsenceType() {
        if (this.absenceType == null) return "NONE";
        else return this.absenceType;
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

    public float getDurationHours() {
        return durationHours;
    }

    public int getDurationDays() {
        return durationDays;
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