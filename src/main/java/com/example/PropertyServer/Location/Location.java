package com.example.PropertyServer.Location;

import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@Embeddable
public class Location {

    @Min(value = 1, message = "location must have a number")
    private int number;

    @NotEmpty(message = "location must have a street")
    private String street;

    @NotEmpty(message = "location must have a city")
    private String city;

    private String county;

    @NotNull(message = "postcode must not be null")
    @Pattern(regexp = "^[A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2}$", message = "${validatedValue} is not a valid postcode")
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
