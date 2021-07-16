package com.example.PropertyDemo.Property;

import javax.persistence.Embeddable;

@Embeddable
public class Location {

    private int number;
    private String street;
    private String city;
    private String county;
    private String postCode;

    public Location() {
    }

    public Location(int number, String street, String city, String county, String postCode) {
        this.number = number;
        this.street = street;
        this.city = city;
        this.county = county;
        this.postCode = postCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}
