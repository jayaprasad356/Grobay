package com.greymatter.grozzer.Model;

public class Address {
    private String name,contact,address,pincode;

    public Address() {
    }

    public Address(String name, String contact, String address, String pincode) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.pincode = pincode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
