package com.example.PropertyDemo.Property;


import com.example.PropertyDemo.Agent.Agent;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import java.net.URL;
import java.util.List;

@Entity
@Inheritance
public abstract class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private PropertyType type;
    private Location location;
    private int bedrooms;

    @ElementCollection
    private List<URL> images;

    @JsonIgnore
    @ManyToOne
    private Agent agent;

    public Property() {
    }

    public Property(PropertyType type, Location location, int bedrooms, List<URL> images, Agent agent) {
        this.type = type;
        this.location = location;
        this.bedrooms = bedrooms;
        this.images = images;
        this.agent = agent;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public List<URL> getImages() {
        return images;
    }

    public void setImages(List<URL> images) {
        this.images = images;
    }

    public void addImage(URL image) {
        this.images.add(image);
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
