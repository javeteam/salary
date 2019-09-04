package com.aspect.salary.entity;

import org.springframework.format.annotation.DateTimeFormat;

import static com.aspect.salary.utils.CommonUtils.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.aspect.salary.utils.CommonUtils.roundValue;

public class Employee {

    private Integer id;
    private int bitrixUserId;
    private boolean active;
    private float salary;
    private float paymentToCard;
    private float bonus;
    private float managementBonus;
    private String email;
    private String name;
    private String surname;
    private String xtrfName;
    private LocalDate hireDate;
    private LocalDate dismissDate;
    private Position position;
    private int vacationDaysLeft;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime workingDayStart;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime lunchStart;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime lunchEnd;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime workingDayEnd;
    private String finalInvoiceUuid;

    private List<Absence> absences = new ArrayList<>();
    private List<CSVAbsence> csvAbsences = new ArrayList<>();
    private List<List<Absence>> intersectionsList = new ArrayList<>();


    public Employee(){
        this.position = Position.Other;
        this.workingDayStart = LocalTime.of(9,0);
        this.lunchStart = LocalTime.of(13,0);
        this.lunchEnd = LocalTime.of(14,0);
        this.workingDayEnd = LocalTime.of(18,0);
        this.active = true;
        this.hireDate = LocalDate.now();
    }

    public Employee(Integer id, String name, String surname){
        this.bitrixUserId = id;
        this.name = name;
        this.surname = surname;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBitrixUserId(int bitrixUserId) {
        this.bitrixUserId = bitrixUserId;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public void setPaymentToCard(float paymentToCard) {
        this.paymentToCard = paymentToCard;
    }

    public void setBonus(float bonus) {
        this.bonus = bonus;
    }

    public void setManagementBonus(float managementBonus) {
        this.managementBonus = managementBonus;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setXtrfName(String xtrfName) {
        this.xtrfName = xtrfName;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setVacationDaysLeft(int vacationDaysLeft) {
        this.vacationDaysLeft = vacationDaysLeft;
    }

    public void setWorkingDayStart(LocalTime workingDayStart) {
        this.workingDayStart = workingDayStart;
    }

    public void setLunchStart(LocalTime lunchStart) {
        this.lunchStart = lunchStart;
    }

    public void setLunchEnd(LocalTime lunchEnd) {
        this.lunchEnd = lunchEnd;
    }

    public void setWorkingDayEnd(LocalTime workingDayEnd) {
        this.workingDayEnd = workingDayEnd;
    }

    public void addAbsence(Absence absence){
        this.absences.add(absence);
    }

    public List<Absence> getAbsences(){
        return absences;
    }

    public int getAbsencesAmount(){
        return absences.size();
    }

    public String getName(){
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isActive() {
        return this.active;
    }

    public int getBitrixUserId() {
        return bitrixUserId;
    }

    public String getXtrfName() {
        return xtrfName;
    }

    public String getEmail() {
        return email;
    }

    public Position getPosition() {
        return position;
    }

    public Integer getId() {
        return id;
    }

    public float getSalary() {
        return salary;
    }

    public float getPaymentToCard() {
        return paymentToCard;
    }

    public float getBonus() {
        return bonus;
    }

    public float getManagementBonus() {
        return managementBonus;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getDismissDate() {
        return dismissDate;
    }

    public void setDismissDate(LocalDate dismissDate) {
        this.dismissDate = dismissDate;
    }

    public List<List<Absence>> getIntersectionsList() {
        return intersectionsList;
    }

    public void setIntersectionsList(List<List<Absence>> intersectionsList) {
        this.intersectionsList = intersectionsList;
    }

    public LocalTime getWorkingDayStart() {
        return workingDayStart;
    }

    public LocalTime getLunchStart() {
        return lunchStart;
    }

    public LocalTime getLunchEnd() {
        return lunchEnd;
    }

    public LocalTime getWorkingDayEnd() {
        return workingDayEnd;
    }

    public String getFinalInvoiceUuid() {
        return finalInvoiceUuid;
    }

    public void setFinalInvoiceUuid(String finalInvoiceUuid) {
        this.finalInvoiceUuid = finalInvoiceUuid;
    }

    public int getVacationDaysLeft() {
        return vacationDaysLeft;
    }

    public float getWorkingDayDuration(){
        float workingDayStartInSeconds = workingDayStart.toSecondOfDay();
        float workingDayEndInSeconds = workingDayEnd.toSecondOfDay();
        float lunchStartInSeconds = lunchStart.toSecondOfDay();
        float lunchEndInSeconds = lunchEnd.toSecondOfDay();
        float rawWorkingDayDurationInSeconds = (workingDayEndInSeconds - workingDayStartInSeconds) - (lunchEndInSeconds - lunchStartInSeconds);
        return roundValue(rawWorkingDayDurationInSeconds / 3600 , 2);
    }

    public List<CSVAbsence> getCSVAbsences() {
        return csvAbsences;
    }

    public void addCSVAbsence(CSVAbsence csvAbsence) {
        if(csvAbsence != null) this.csvAbsences.add(csvAbsence);
    }

}
