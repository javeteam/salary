package com.aspect.salary.form;

import com.aspect.salary.utils.CommonUtils;

import java.time.LocalDateTime;

import static com.aspect.salary.utils.CommonUtils.currencyFormatter;

public class PaymentForm {

    private Integer id;
    private LocalDateTime creationDate;
    private boolean complete;
    private boolean notificationSent;
    private int totalAmount;

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

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getFormattedTotalAmount() {
        return currencyFormatter(totalAmount);
    }

    public String getFormattedCreationDate (){
        return this.creationDate.format(CommonUtils.jDateTimeFormatter);
    }
}
