package com.example.PropertyDemo;

import javax.persistence.Embeddable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return number == location.number && street.equals(location.street) && city.equals(location.city)
                && county.equals(location.county) && postCode.equals(location.postCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, street, city, county, postCode);
    }
}
