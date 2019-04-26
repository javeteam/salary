package com.aspect.salary.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Invoice {

    private final float WORKING_DAYS_PER_MONTH = 21.5f;
    private final float OVERTIME_RATE = 1.25f;

    private int employeeId;
    private String username;
    private boolean confirmed;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private float salary;
    private float officialSalary;
    private float bonus;
    private List<List<Absence>> absenceIntersection;
    private List<Absence> vacation = new ArrayList<>();
    private List<Absence> overtime = new ArrayList<>();
    private List<Absence> unpaidLeave = new ArrayList<>();
    private List<Absence> sickLeave = new ArrayList<>();


    public Invoice(){
        this.creationDate = LocalDateTime.now();
        this.confirmed = false;
    }

    public Invoice(Employee employee){
        this();
        this.employeeId = employee.getId();
        this.salary = employee.getSalary();
        this.officialSalary = employee.getOfficialSalary();
        this.bonus = employee.getBonus();
        this.absenceIntersection = employee.getIntersectionsList();
        this.username = employee.getName() + " " + employee.getSurname();

        List<Absence> absenceList = employee.getAbsences();

        if(absenceList.size() != 0){
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
                }
            }
        }
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

    public String getPaidPeriod() {
        return this.creationDate.minusMonths(1).toLocalDate().format(DateTimeFormatter.ofPattern("MM.yyyy"));
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

    public void setOfficialSalary(float officialSalary) {
        this.officialSalary = officialSalary;
    }

    public int getFreelanceHourPrise() {
        float freelanceHourPrise = salary / (WORKING_DAYS_PER_MONTH * 8);
        return Math.round(freelanceHourPrise);
    }

    public int getWorkingDayPrise() {
        float workingDarPrise = salary / WORKING_DAYS_PER_MONTH;
        return Math.round(workingDarPrise);
    }

    public int getOvertimeHourPrise() {
        float overtimeHourPrise = salary * OVERTIME_RATE / (WORKING_DAYS_PER_MONTH * 8);
        return Math.round(overtimeHourPrise);
    }

    public String getUsername() {
        return username;
    }

    public List<Absence> getVacation() {
        return vacation;
    }

    public List<Absence> getOvertime() {
        return overtime;
    }

    public List<Absence> getUnpaidLeave() {
        return unpaidLeave;
    }

    public List<Absence> getSickLeave() {
        return sickLeave;
    }

    public int getOfficialSalary() {
        return Math.round(officialSalary);
}

    public int getBonus() {
        return Math.round(bonus);
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

    public List<List<Absence>> getAbsenceIntersection() {
        return absenceIntersection;
    }

    public int getTotalAmount (){
        int totalAmount;
        int overtimeAndUnpaidLeavePrise = getWeightedOvertimeAndUnpaidLeavePrise();
        int vacationPrise = getAbsenceGroupPrise(vacation);
        totalAmount = Math.round(salary) + Math.round(bonus) + overtimeAndUnpaidLeavePrise + vacationPrise - Math.round(officialSalary);
        return totalAmount;
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
}
