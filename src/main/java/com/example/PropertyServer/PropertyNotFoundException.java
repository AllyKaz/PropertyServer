package com.example.PropertyServer;

public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(int id) {
        super("Property with id = " + id + " not found.");
    }


}
