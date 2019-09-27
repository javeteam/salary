package com.aspect.salary.entity;

import static com.aspect.salary.utils.CommonUtils.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.aspect.salary.utils.CommonUtils.roundValue;

public class Invoice {

    private final float WORKING_DAYS_PER_MONTH = 21.5f;
    private final float OVERTIME_RATE = 1.25f;

    private Integer id;
    private int employeeId;
    private String username;
    private boolean confirmed;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private float salary;
    private float paymentToCard;
    private float bonus;
    private float managementBonus;
    private String uuid;
    private float workingDayDuration;
    private String notes;
    private int vacationDaysLeft;
    private LocalDate paidFrom;
    private LocalDate paidUntil;
    private List<List<Absence>> absenceIntersection;
    private List<Absence> absenceList = new ArrayList<>();
    private List<InvoiceItem> items = new ArrayList<>();



    public Invoice(){
        this.creationDate = LocalDateTime.now();
        this.modificationDate = LocalDateTime.now();
        this.confirmed = false;
        this.notes = "";
    }

    public Invoice(Employee employee){
        this();
        this.employeeId = employee.getId();
        this.salary = employee.getSalary();
        this.paymentToCard = employee.getPaymentToCard();
        this.bonus = employee.getBonus();
        this.managementBonus = employee.getManagementBonus();
        this.workingDayDuration = employee.getWorkingDayDuration();
        this.username = employee.getSurname() + " " + employee.getName();
        this.uuid  = UUID.randomUUID().toString();
        this.vacationDaysLeft = employee.getVacationDaysLeft();

        if(employee.getHireDate().isAfter(LocalDate.now().withDayOfMonth(1).minusMonths(1))){
            int daysAfterHiring = 0;
            for (int dayNumber = employee.getHireDate(). getDayOfMonth(); dayNumber <= employee.getHireDate().lengthOfMonth(); dayNumber++){
                if (employee.getHireDate().withDayOfMonth(dayNumber).getDayOfWeek().getValue() <= 5){
                    daysAfterHiring++;
                }
            }
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setDescription("Утримання за невідпрацьовані дні");
            invoiceItem.setPrise(Math.round( (daysAfterHiring/WORKING_DAYS_PER_MONTH - 1) * salary) );
            if (invoiceItem.getPrise() < 0) this.addItem(invoiceItem);
        }

        /*List<CSVAbsence> csvAbsenceList = employee.getCSVAbsences();

        if(csvAbsenceList.size() != 0){
            for(CSVAbsence csvAbsence : csvAbsenceList){
                float durationHours = 0;

                if(csvAbsence.getAbsenceType().equals("OVERTIME")) {
                    durationHours = (float)csvAbsence.getPrise() / this.getOvertimeHourPrise();
                } else if(csvAbsence.getAbsenceType().equals("FREELANCE")){
                    durationHours = (float) csvAbsence.getPrise() / this.getFreelanceHourPrise();
                }
                durationHours = roundValue(durationHours,2);
                absenceList.add(new Absence(csvAbsence.getAbsenceType(),durationHours, Weight.POSITIVE ));
            }
        }
        */
    }

    public void setCSVAbsences(List<CSVAbsence> csvAbsenceList){
        if(csvAbsenceList.size() != 0){
            for(CSVAbsence csvAbsence : csvAbsenceList){
                addCSVAbsence(csvAbsence);
            }
        }
    }

    public void addCSVAbsence (CSVAbsence csvAbsence){
        float durationHours = 0;

        if(csvAbsence.getAbsenceType().equals("OVERTIME")) {
            durationHours = (float)csvAbsence.getPrise() / this.getOvertimeHourPrise();
        } else if(csvAbsence.getAbsenceType().equals("FREELANCE")){
            durationHours = (float) csvAbsence.getPrise() / this.getFreelanceHourPrise();
        }
        durationHours = roundValue(durationHours,2);
        this.absenceList.add(new Absence(csvAbsence.getAbsenceType(),durationHours, Weight.POSITIVE ));
    }


    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public int getSalary() {
        return Math.round(salary);
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public void setBonus(float bonus) {
        this.bonus = bonus;
    }

    public void setManagementBonus(float managementBonus) {
        this.managementBonus = managementBonus;
    }

    public void setPaymentToCard(float paymentToCard) {
        this.paymentToCard = paymentToCard;
    }

    public float getWorkingDayDuration() {
        return workingDayDuration;
    }

    public void setWorkingDayDuration(float workingDayDuration) {
        this.workingDayDuration = workingDayDuration;
    }

    public int getVacationDaysLeft() {
        return vacationDaysLeft;
    }

    public void setVacationDaysLeft(int vacationDaysLeft) {
        this.vacationDaysLeft = vacationDaysLeft;
    }

    public int getFreelanceHourPrise() {
        float freelanceHourPrise = salary / (WORKING_DAYS_PER_MONTH * workingDayDuration);
        return Math.round(freelanceHourPrise);
    }

    public int getWorkingDayPrise() {
        float workingDarPrise = salary / WORKING_DAYS_PER_MONTH;
        return Math.round(workingDarPrise);
    }

    public int getOvertimeHourPrise() {
        float overtimeHourPrise = salary * OVERTIME_RATE / (WORKING_DAYS_PER_MONTH * workingDayDuration);
        return Math.round(overtimeHourPrise);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPaymentToCard() {
        return Math.round(paymentToCard);
}

    public int getBonus() {
        return Math.round(bonus);
    }

    public int getManagementBonus() {
        return Math.round(managementBonus);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setAbsenceIntersection(List<List<Absence>> absenceIntersection) {
        this.absenceIntersection = absenceIntersection;
    }

    public List<List<Absence>> getAbsenceIntersection() {
        return absenceIntersection;
    }

    public List<Absence> getAbsences() {
        return absenceList;
    }

    public void setAbsences(List<Absence> absenceList) {
        this.absenceList = absenceList;
    }

    public void addAbsences(List<Absence> absenceList) {
        this.absenceList.addAll(absenceList);
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public void addItem(InvoiceItem item){
        this.items.add(item);
    }

    public LocalDate getPaidFrom() {
        return paidFrom;
    }

    public void setPaidFrom(LocalDate paidFrom) {
        this.paidFrom = paidFrom;
    }

    public LocalDate getPaidUntil() {
        return paidUntil;
    }

    public void setPaidUntil(LocalDate paidUntil) {
        this.paidUntil = paidUntil;
    }
}
