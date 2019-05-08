package com.aspect.salary.entity;

import com.aspect.salary.utils.CommonUtils.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.aspect.salary.utils.CommonUtils.roundValue;

public class Employee {

    private int id;
    private int bitrixUserId;
    private float salary;
    private float officialSalary;
    private float bonus;
    private String email;
    private String name;
    private String surname;
    private String xtrfName;
    private Position position;
    private int vacationDaysLeft;
    private LocalTime workingDayStart;
    private LocalTime lunchStart;
    private LocalTime lunchEnd;
    private LocalTime workingDayEnd;

    private List<Absence> absences = new ArrayList<>();
    private List<CSVAbsence> csvAbsences = new ArrayList<>();
    private List<List<Absence>> intersectionsList = new ArrayList<>();


    public Employee(int id, int bitrixUserId, float salary, float officialSalary, float bonus, String email, String name, String surname, String xtrfName, Position position, int vacationDaysLeft, LocalTime workingDayStart, LocalTime workingDayEnd, LocalTime lunchStart, LocalTime lunchEnd) {
        this.id = id;
        this.bitrixUserId = bitrixUserId;
        this.salary = salary;
        this.officialSalary = officialSalary;
        this.bonus = bonus;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.xtrfName = xtrfName;
        this.position = position;
        this.vacationDaysLeft = vacationDaysLeft;
        this.workingDayStart = workingDayStart;
        this.workingDayEnd = workingDayEnd;
        this.lunchStart = lunchStart;
        this.lunchEnd = lunchEnd;

    }

    public Employee(int id, String name, String surname){
        this.bitrixUserId = id;
        this.name = name;
        this.surname = surname;
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

    public int getBitrixUserId() {
        return bitrixUserId;
    }

    public String getXtrfName() {
        return xtrfName;
    }

    public Position getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public float getSalary() {
        return salary;
    }

    public float getOfficialSalary() {
        return officialSalary;
    }

    public float getBonus() {
        return bonus;
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

    public float getWorkingDayDuration(){
        float workingDayStartInSeconds = workingDayStart.getHour() * 3600 + workingDayStart.getMinute() * 60;
        float workingDayEndInSeconds = workingDayEnd.getHour() * 3600 + workingDayEnd.getMinute() * 60;
        float lunchStartInSeconds = lunchStart.getHour() * 3600 + lunchStart.getMinute() * 60;
        float lunchEndInSeconds = lunchEnd.getHour() * 3600 + lunchEnd.getMinute() * 60;
        float rawWorkingDayDurationInSeconds = (workingDayEndInSeconds - workingDayStartInSeconds) - (lunchEndInSeconds - lunchStartInSeconds);
        return roundValue(rawWorkingDayDurationInSeconds / 3600 , 2);
    }

    public List<CSVAbsence> getCSVAbsences() {
        return csvAbsences;
    }

    public void addCSVAbsence(CSVAbsence csvAbsences) {
        if(csvAbsences != null) this.csvAbsences.add(csvAbsences);
    }
}
