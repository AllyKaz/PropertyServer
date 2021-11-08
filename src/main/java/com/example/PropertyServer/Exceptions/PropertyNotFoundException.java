package com.example.PropertyServer.Exceptions;

public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(int id) {
        super("Property with id = " + id + " not found.");
    }


}
