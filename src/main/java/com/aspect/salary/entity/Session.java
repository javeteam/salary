package com.aspect.salary.entity;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private boolean csvUploaded = false;
    private boolean officialSalarySpecified = false;
    private List<CSVAbsence> csvAbsenceList = new ArrayList<>();


    public boolean isCsvUploaded() {
        return csvUploaded;
    }

    public void addCSVAbsence (List<CSVAbsence> csvAbsenceList){
        this.csvAbsenceList.addAll(csvAbsenceList);
    }

    public List<CSVAbsence> getCsvAbsenceList() {
        return csvAbsenceList;
    }

    public void setCsvUploaded(boolean csvUploaded) {
        this.csvUploaded = csvUploaded;
    }

    public boolean isOfficialSalarySpecified() {
        return officialSalarySpecified;
    }

    public void setOfficialSalarySpecified(boolean officialSalarySpecified) {
        this.officialSalarySpecified = officialSalarySpecified;
    }

}
