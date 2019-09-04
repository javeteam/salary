package com.aspect.salary.entity;

import static com.aspect.salary.utils.CommonUtils.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Payment {
    private Integer id;
    private LocalDateTime creationDate;
    private boolean complete;
    private int TotalAmount;
    private List<Invoice> invoices = new ArrayList<>();

    public Payment() {
        this.creationDate = LocalDateTime.now();
        this.complete = false;
    }

    public Payment(int id, LocalDateTime creationDate, boolean complete, int TotalAmount) {
        this.id = id;
        this.creationDate = creationDate;
        this.complete = complete;
        this.TotalAmount = TotalAmount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public int getTotalAmount() {
        return TotalAmount;
    }

    public String getFormattedTotalAmount() {
        return currencyFormatter(TotalAmount);
    }

    public void setTotalAmount(int totalAmount) {
        this.TotalAmount = totalAmount;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void addInvoice(Invoice invoice) {
        invoices.add(invoice);
    }

    public String getPaidPeriod() {
        return this.creationDate.minusMonths(1).toLocalDate().format(DateTimeFormatter.ofPattern("MM.yyyy"));
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}
