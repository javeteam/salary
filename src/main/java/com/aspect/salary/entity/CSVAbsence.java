package com.aspect.salary.entity;

public class CSVAbsence {
    private String employeeXtrfName;
    private String absenceType;
    private int prise;

    public CSVAbsence (String employeeXtrfName, String absenceType, int prise){
        this.employeeXtrfName = employeeXtrfName;
        this.absenceType = absenceType;
        this.prise = prise;
    }

    public String getEmployeeXtrfName() {
        return employeeXtrfName;
    }

    public String getAbsenceType() {
        return absenceType;
    }

    public int getPrise() {
        return prise;
    }

}
