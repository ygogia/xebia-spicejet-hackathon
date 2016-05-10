package com.flightish.model;

public class MenuDTO {
    private String company_mail;

    private String commpany_name;

    private String price;

    private String item_name;

    private String item_id;

    public String getCompany_mail() {
        return company_mail;
    }

    public void setCompany_mail(String company_mail) {
        this.company_mail = company_mail;
    }

    public String getCommpany_name() {
        return commpany_name;
    }

    public void setCommpany_name(String commpany_name) {
        this.commpany_name = commpany_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }
}