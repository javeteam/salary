package com.aspect.salary.entity;

import com.aspect.salary.utils.CommonUtils.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.aspect.salary.utils.CommonUtils.currencyFormatter;
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
    private float officialSalary;
    private float bonus;
    private String uuid;
    private float workingDayDuration;
    private List<List<Absence>> absenceIntersection;
    private List<Absence> vacation = new ArrayList<>();
    private List<Absence> overtime = new ArrayList<>();
    private List<Absence> unpaidLeave = new ArrayList<>();
    private List<Absence> sickLeave = new ArrayList<>();
    private List<Absence> freelance = new ArrayList<>();



    public Invoice(){
        this.creationDate = LocalDateTime.now();
        this.modificationDate = LocalDateTime.now();
        this.confirmed = false;
    }

    public Invoice(Employee employee){
        this();
        this.employeeId = employee.getId();
        this.salary = employee.getSalary();
        this.officialSalary = employee.getOfficialSalary();
        this.bonus = employee.getBonus();
        this.workingDayDuration = employee.getWorkingDayDuration();
        this.absenceIntersection = employee.getIntersectionsList();
        this.username = employee.getSurname() + " " + employee.getName();
        this.uuid  = UUID.randomUUID().toString();

        List<Absence> absenceList = employee.getAbsences();
        List<CSVAbsence> csvAbsenceList = employee.getCSVAbsences();
        setAbsences(absenceList);

        if(csvAbsenceList.size() != 0){
            for(CSVAbsence csvAbsence : csvAbsenceList){
                float durationHours = 0;

                if(csvAbsence.getAbsenceType().equals("OVERTIME")) {
                    durationHours = (float)csvAbsence.getPrise() / this.getOvertimeHourPrise();
                    durationHours = roundValue(durationHours,2);
                    overtime.add(new Absence(csvAbsence.getAbsenceType(),durationHours, Weight.POSITIVE ));
                } else if(csvAbsence.getAbsenceType().equals("FREELANCE")){
                    durationHours = (float) csvAbsence.getPrise() / this.getFreelanceHourPrise();
                    durationHours = roundValue(durationHours,2);
                    freelance.add(new Absence(csvAbsence.getAbsenceType(),durationHours, Weight.POSITIVE ));
                }
            }
        }
    }

    public Invoice(int id, int employeeId, int salary, int officialSalary, int bonus, float workingDayDuration, boolean isConfirmed, LocalDateTime creationDate, LocalDateTime modificationDate, String username, String  uuid ){
        this.id = id;
        this.employeeId = employeeId;
        this.salary = salary;
        this.officialSalary = officialSalary;
        this.bonus = bonus;
        this.workingDayDuration = workingDayDuration;
        this.confirmed = isConfirmed;
        this.creationDate = creationDate;
        this.username = username;
        this.uuid = uuid;
    }

    public void setAbsences(List<Absence> absenceList){
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

    public float getWorkingDayDuration() {
        return workingDayDuration;
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

    public List<Absence> getFreelance() {
        return freelance;
    }

    public int getOfficialSalary() {
        return Math.round(officialSalary);
}

    public int getBonus() {
        return Math.round(bonus);
    }

    public String getUuid() {
        return uuid;
    }

    public String getPaidPeriod() {
        return this.creationDate.minusMonths(1).toLocalDate().format(DateTimeFormatter.ofPattern("MM.yyyy"));
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
}
