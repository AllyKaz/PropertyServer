package com.example.PropertyDemo.Property;

import com.example.PropertyDemo.Agent.Agent;

import javax.persistence.Entity;
import java.net.URL;
import java.util.List;

@Entity
public class SaleProperty extends Property {

    private double price;

    public SaleProperty() {
    }

    public SaleProperty(PropertyType type, Location location, int bedrooms, List<URL> images, Agent agent, double price) {
        super(type, location, bedrooms, images, agent);
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
