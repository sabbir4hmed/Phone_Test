package com.sabbir.walton.mmitest.TestActivities;

public class DataModel {


    private String name;
    private String address;

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    private String contact;

    public DataModel(String name, String address, String contact) {
        this.name = name;
        this.address = address;
        this.contact = contact;
    }


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

public DataModel()
{

}



}
