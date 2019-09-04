package com.aspect.salary.entity;

public class InvoiceItem {

    private Integer id;
    private String description;
    private int prise;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrise() {
        return prise;
    }

    public void setPrise(int prise) {
        this.prise = prise;
    }

    public boolean isNotEmpty (){
        return (this.prise != 0 && this.description != null && this.description.length() > 0);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
