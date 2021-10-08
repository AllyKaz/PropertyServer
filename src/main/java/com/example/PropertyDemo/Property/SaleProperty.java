package com.example.PropertyDemo.Property;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Location.Location;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.List;

@Entity
public class SaleProperty extends Property {

    @NotNull(message = "price must not be null")
    private int price;

    public SaleProperty() {
    }

    public SaleProperty(PropertyType type, Location location, int bedrooms, List<URL> images, Agent agent, int price) {
        super(type, location, bedrooms, images, agent);
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
