package com.example.PropertyDemo.Builders;

import com.example.PropertyDemo.Location.Location;

public class LocationBuilder {

    private int number;
    private String street;
    private String city;
    private String postcode;
    private String county;

    public LocationBuilder withNumber(int number) {
        this.number = number;
        return this;
    }

    public LocationBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public LocationBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public LocationBuilder withPostcode(String postcode) {
        this.postcode = postcode;
        return this;
    }

    public LocationBuilder withCounty(String county) {
        this.county = county;
        return this;
    }
    
    public Location build() {
        return new Location(number, street, city, county, postcode);
    }
}
