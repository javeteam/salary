package com.aspect.salary.entity;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
    private int vacationDaysLeft;
    private LocalTime workingDayStart;
    private LocalTime lunchStart;
    private LocalTime lunchEnd;
    private LocalTime workingDayEnd;

    private List<Absence> absences = new ArrayList<>();
    private List<List<Absence>> intersectionsList = new ArrayList<>();

    private float freelanceHourPrise;
    private float overtimeHourPrise;


    public Employee(int id, int bitrixUserId, float salary, float officialSalary, float bonus, String email, String name, String surname, String xtrfName, int vacationDaysLeft, Time workingDayStart, Time workingDayEnd, Time lunchStart, Time lunchEnd) {
        this.id = id;
        this.bitrixUserId = bitrixUserId;
        this.salary = salary;
        this.officialSalary = officialSalary;
        this.bonus = bonus;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.xtrfName = xtrfName;
        this.vacationDaysLeft = vacationDaysLeft;
        this.workingDayStart = workingDayStart.toLocalTime();
        this.workingDayEnd = workingDayEnd.toLocalTime();
        this.lunchStart = lunchStart.toLocalTime();
        this.lunchEnd = lunchEnd.toLocalTime();

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
}
