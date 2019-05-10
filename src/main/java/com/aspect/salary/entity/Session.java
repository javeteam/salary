package com.aspect.salary.entity;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private boolean csvUploaded = false;
    private boolean paymentToCardSpecified = false;
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

    public boolean isPaymentToCardSpecified() {
        return paymentToCardSpecified;
    }

    public void setPaymentToCardSpecified(boolean paymentToCardSpecified) {
        this.paymentToCardSpecified = paymentToCardSpecified;
    }

}
