package com.example.PropertyDemo.Property;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Location.Location;

import javax.persistence.Entity;
import java.net.URL;
import java.util.List;

@Entity
public class RentalProperty extends Property {

    private int monthlyRent;

    public RentalProperty() {
    }

    public RentalProperty(PropertyType type, Location location, int bedrooms, List<URL> images, Agent agent, int monthlyRent) {
        super(type, location, bedrooms, images, agent);
        this.monthlyRent = monthlyRent;
    }

    public int getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(int monthlyRent) {
        this.monthlyRent = monthlyRent;
    }




}
