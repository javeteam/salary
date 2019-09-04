package com.aspect.salary.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Session {

    private boolean csvUploaded = false;
    private boolean paymentToCardSpecified = false;
    private Map<String, Invoice> invoiceMap = new HashMap<>();
    private List<CSVAbsence> csvAbsenceList = new ArrayList<>();
    private List<Employee> employeeCardPayments;



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

    public List<Employee> getEmployeeCardPayments() {
        return employeeCardPayments;
    }

    public void setEmployeeCardPayments(List<Employee> employeeCardPayments) {
        this.employeeCardPayments = employeeCardPayments;
    }

    public Invoice getInvoice(String uuid) {
        return this.invoiceMap.get(uuid);
    }

    public void addInvoice(Invoice invoice) {
        invoiceMap.put(invoice.getUuid(), invoice);
    }

}
