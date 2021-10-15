package com.example.PropertyServer.Property;

import com.example.PropertyServer.Agent.Agent;
import com.example.PropertyServer.Location.Location;

import javax.persistence.Entity;
import java.net.URL;
import java.util.List;

@Entity
public class SaleProperty extends Property {

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
