package com.aspect.salary.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Payment {
    private int id;
    private LocalDateTime creationDate;
    private boolean complete;
    private float TotalAmount;
    private List<Invoice> invoices = new ArrayList<>();

    public Payment(){
        this.id = -1;
        this.creationDate = LocalDateTime.now();
        this.complete = false;
    }

    public Payment(int id, LocalDateTime creationDate, boolean complete, float TotalAmount){
        this.id = id;
        this.creationDate = creationDate;
        this.complete = complete;
        this.TotalAmount = TotalAmount;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public float getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.TotalAmount = totalAmount;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void addInvoice (Invoice invoice){
        invoices.add(invoice);
    }
}
