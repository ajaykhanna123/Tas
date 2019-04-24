package com.chicmic.task5;

import java.io.Serializable;

public class Contact implements Serializable {
    private String name;
    private String phnNo;
    private String address;
    private String gender;
    private String email;
    private String faxNo;
    private long time;
    private String imageId;


    public Contact(long time, String name, String phnNo, String address, String gender, String email,
                   String faxNo, String imageId) {
        this.name = name;
        this.phnNo = phnNo;
        this.address = address;
        this.gender = gender;
        this.email = email;
        this.faxNo = faxNo;
        this.time = time;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhnNo() {
        return phnNo;
    }

    public void setPhnNo(String phnNo) {
        this.phnNo = phnNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFaxNo() {
        return faxNo;
    }

    public void setFaxNo(String faxNo) {
        this.faxNo = faxNo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long uniqueId) {
        this.time = uniqueId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}