package com.aspect.salary.form;

import com.aspect.salary.utils.CommonUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.aspect.salary.utils.CommonUtils.currencyFormatter;

public class EmployeeForm {

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
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate hireDate;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate dismissDate;
    private CommonUtils.Position position;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getBitrixUserId() {
        return bitrixUserId;
    }

    public void setBitrixUserId(int bitrixUserId) {
        this.bitrixUserId = bitrixUserId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public float getPaymentToCard() {
        return paymentToCard;
    }

    public void setPaymentToCard(float paymentToCard) {
        this.paymentToCard = paymentToCard;
    }

    public float getBonus() {
        return bonus;
    }

    public void setBonus(float bonus) {
        this.bonus = bonus;
    }

    public float getManagementBonus() {
        return managementBonus;
    }

    public void setManagementBonus(float managementBonus) {
        this.managementBonus = managementBonus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getXtrfName() {
        return xtrfName;
    }

    public void setXtrfName(String xtrfName) {
        this.xtrfName = xtrfName;
    }

    public CommonUtils.Position getPosition() {
        return position;
    }

    public void setPosition(CommonUtils.Position position) {
        this.position = position;
    }

    public int getVacationDaysLeft() {
        return vacationDaysLeft;
    }

    public void setVacationDaysLeft(int vacationDaysLeft) {
        this.vacationDaysLeft = vacationDaysLeft;
    }

    public LocalTime getWorkingDayStart() {
        return workingDayStart;
    }

    public void setWorkingDayStart(LocalTime workingDayStart) {
        this.workingDayStart = workingDayStart;
    }

    public LocalTime getLunchStart() {
        return lunchStart;
    }

    public void setLunchStart(LocalTime lunchStart) {
        this.lunchStart = lunchStart;
    }

    public LocalTime getLunchEnd() {
        return lunchEnd;
    }

    public void setLunchEnd(LocalTime lunchEnd) {
        this.lunchEnd = lunchEnd;
    }

    public LocalTime getWorkingDayEnd() {
        return workingDayEnd;
    }

    public void setWorkingDayEnd(LocalTime workingDayEnd) {
        this.workingDayEnd = workingDayEnd;
    }

    public String getFormattedCurrency (float value){
        return currencyFormatter(Math.round(value));
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

    public String getFinalInvoiceUuid() {
        return finalInvoiceUuid;
    }

    public void setFinalInvoiceUuid(String finalInvoiceUuid) {
        this.finalInvoiceUuid = finalInvoiceUuid;
    }
}
