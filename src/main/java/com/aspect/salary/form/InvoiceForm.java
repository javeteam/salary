package com.aspect.salary.form;


import com.aspect.salary.entity.Absence;
import com.aspect.salary.entity.InvoiceItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.aspect.salary.utils.CommonUtils.currencyFormatter;
import static com.aspect.salary.utils.CommonUtils.monthDateFormatter;

public class InvoiceForm {

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
    private int freelanceHourPrise;
    private int workingDayPrise;
    private int overtimeHourPrise;
    private List<List<Absence>> absenceIntersection;
    private List<Absence> vacation = new ArrayList<>();
    private List<Absence> overtime = new ArrayList<>();
    private List<Absence> unpaidLeave = new ArrayList<>();
    private List<Absence> sickLeave = new ArrayList<>();
    private List<Absence> freelance = new ArrayList<>();
    private List<InvoiceItem> items = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public float getWorkingDayDuration() {
        return workingDayDuration;
    }

    public void setWorkingDayDuration(float workingDayDuration) {
        this.workingDayDuration = workingDayDuration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getVacationDaysLeft() {
        return vacationDaysLeft;
    }

    public void setVacationDaysLeft(int vacationDaysLeft) {
        this.vacationDaysLeft = vacationDaysLeft;
    }

    public List<List<Absence>> getAbsenceIntersection() {
        return absenceIntersection;
    }

    public void setAbsenceIntersection(List<List<Absence>> absenceIntersection) {
        this.absenceIntersection = absenceIntersection;
    }

    public List<Absence> getVacation() {
        return vacation;
    }

    public void setVacation(List<Absence> vacation) {
        this.vacation = vacation;
    }

    public List<Absence> getOvertime() {
        return overtime;
    }

    public void setOvertime(List<Absence> overtime) {
        this.overtime = overtime;
    }

    public List<Absence> getUnpaidLeave() {
        return unpaidLeave;
    }

    public void setUnpaidLeave(List<Absence> unpaidLeave) {
        this.unpaidLeave = unpaidLeave;
    }

    public List<Absence> getSickLeave() {
        return sickLeave;
    }

    public void setSickLeave(List<Absence> sickLeave) {
        this.sickLeave = sickLeave;
    }

    public List<Absence> getFreelance() {
        return freelance;
    }

    public void setFreelance(List<Absence> freelance) {
        this.freelance = freelance;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public int getFreelanceHourPrise() {
        return freelanceHourPrise;
    }

    public void setFreelanceHourPrise(int freelanceHourPrise) {
        this.freelanceHourPrise = freelanceHourPrise;
    }

    public int getWorkingDayPrise() {
        return workingDayPrise;
    }

    public void setWorkingDayPrise(int workingDayPrise) {
        this.workingDayPrise = workingDayPrise;
    }

    public int getOvertimeHourPrise() {
        return overtimeHourPrise;
    }

    public void setOvertimeHourPrise(int overtimeHourPrise) {
        this.overtimeHourPrise = overtimeHourPrise;
    }

    public void addAbsences(List<Absence> absenceList){
        for (Absence absence : absenceList){
            switch (absence.getAbsenceType()){
                case ("VACATION"):
                    vacation.add(absence);
                    break;
                case ("OVERTIME"):
                    overtime.add(absence);
                    break;
                case ("LEAVESICK"):
                    sickLeave.add(absence);
                    break;
                case ("LEAVEUNPAYED"):
                    unpaidLeave.add(absence);
                    break;
                case ("FREELANCE"):
                    freelance.add(absence);
                    break;
            }
        }
    }

    public String getFormattedCurrency(int value){
        return currencyFormatter(value);
    }

    public String getLocalizedAbsenceType(Absence absence){
        switch (absence.getAbsenceType()) {
            case ("VACATION"):
                return "Відпустка";
            case ("OVERTIME"):
                return "Овертайм";
            case ("LEAVESICK"):
                return "Лікарняний";
            case ("LEAVEUNPAYED"):
                return "Години за свій кошт";
            default: return absence.getAbsenceType();
        }
    }
    public String getPaidPeriod() {
        return this.creationDate.minusMonths(1).toLocalDate().format(monthDateFormatter);
    }

    public float getAbsenceGroupDuration (List<Absence> absenceList){
        float totalDuration = 0;
        for (Absence absence : absenceList){
            totalDuration += absence.getDuration() * absence.getCoefficient();
        }
        return totalDuration;
    }

    public int getAbsencePrise(Absence absence){
        switch (absence.getAbsenceType()){

            case "LEAVESICK":
                return 0;
            case "FREELANCE":
                return Math.round(absence.getDuration() * absence.getCoefficient() * getFreelanceHourPrise());
            case "VACATION":
                return Math.round(absence.getDuration() * absence.getCoefficient() * getWorkingDayPrise());
            case "OVERTIME":
                return Math.round(absence.getDuration() * absence.getCoefficient() * getOvertimeHourPrise());
            case "LEAVEUNPAYED":
                return Math.round(absence.getDuration() * absence.getCoefficient() * getFreelanceHourPrise());
            default:
                return 0;
        }
    }

    public int getAbsenceGroupPrise(List<Absence> absenceList){
        int groupPrise = 0;
        for (Absence absence : absenceList){
            groupPrise += getAbsencePrise(absence);
        }
        return groupPrise;

    }

    public float getWeightedOvertimeAndUnpaidLeaveDuration(){
        float overtimeGroupDuration = getAbsenceGroupDuration(getOvertime());
        float unpaidLeaveGroupDuration = getAbsenceGroupDuration(getUnpaidLeave());

        return overtimeGroupDuration + unpaidLeaveGroupDuration;
    }

    public int getWeightedOvertimeAndUnpaidLeavePrise(){
        float duration = getWeightedOvertimeAndUnpaidLeaveDuration();
        float prise = ((duration > 0) ? duration * getOvertimeHourPrise() : duration * getFreelanceHourPrise());
        return Math.round(prise);
    }

    public int getAdditionalItemsPrise (){
        int prise = 0;
        for(InvoiceItem item : this.items){
            prise += item.getPrise();
        }
        return prise;
    }

    public int getFreelancePrise(){
        int prise = 0;
        for(Absence absence : this.freelance){
            prise += absence.getDurationHours() * this.getFreelanceHourPrise();
        }
        return prise;
    }

    public int getTotalAmount (){
        int totalAmount;
        int overtimeAndUnpaidLeavePrise = getWeightedOvertimeAndUnpaidLeavePrise();
        int vacationPrise = getAbsenceGroupPrise(vacation);
        int additionalItemsPrise = getAdditionalItemsPrise();
        int freelancePrise = getFreelancePrise();
        totalAmount = Math.round(salary) + Math.round(bonus) + Math.round(managementBonus) + overtimeAndUnpaidLeavePrise + vacationPrise + additionalItemsPrise + freelancePrise - Math.round(paymentToCard);
        return totalAmount;
    }

    public void addItem(InvoiceItem item){
        this.items.add(item);
    }

}
