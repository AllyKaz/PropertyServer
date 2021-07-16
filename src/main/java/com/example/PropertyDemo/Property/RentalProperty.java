package com.example.PropertyDemo.Property;

import com.example.PropertyDemo.Agent.Agent;

import javax.persistence.Entity;
import java.net.URL;
import java.util.List;

@Entity
public class RentalProperty extends Property {

    private double monthlyRent;

    public RentalProperty() {
    }

    public RentalProperty(PropertyType type, Location location, int bedrooms, List<URL> images, Agent agent, double monthlyRent) {
        super(type, location, bedrooms, images, agent);
        this.monthlyRent = monthlyRent;
    }

    public double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }
}
