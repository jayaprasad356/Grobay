package com.greymatter.grozzer.Model;

public class Extra {
    private String imgUrl,pincode,deliverycharge,details,mail,mobile,whatsapp;

    public Extra() {
    }

    public Extra(String imgUrl, String pincode, String deliverycharge, String details, String mail, String mobile, String whatsapp) {
        this.imgUrl = imgUrl;
        this.pincode = pincode;
        this.deliverycharge = deliverycharge;
        this.details = details;
        this.mail = mail;
        this.mobile = mobile;
        this.whatsapp = whatsapp;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDeliverycharge() {
        return deliverycharge;
    }

    public void setDeliverycharge(String deliverycharge) {
        this.deliverycharge = deliverycharge;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }
}
