package com.example.PropertyDemo.Agent;

import com.example.PropertyDemo.Location.Location;
import com.example.PropertyDemo.Property.Property;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private Location location;

    private String telephoneNumber;
    private URL logoImage;

    @JsonIgnore
    @OneToMany
    private List<Property> properties = new ArrayList<>();

    public Agent() {
    }

    public Agent(String name, Location location, String telephoneNumber) {
        this.name = name;
        this.location = location;
        this.telephoneNumber = telephoneNumber;
    }

    public Agent(String name, Location location, String telephoneNumber, URL logoImage) {
        this.name = name;
        this.location = location;
        this.telephoneNumber = telephoneNumber;
        this.logoImage = logoImage;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public URL getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(URL logoImage) {
        this.logoImage = logoImage;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public void addProperty(Property property) {
        this.properties.add(property);
    }
}
