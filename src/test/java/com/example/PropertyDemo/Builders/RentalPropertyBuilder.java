package com.example.PropertyDemo.Builders;

import com.example.PropertyDemo.Property.RentalProperty;

public interface RentalPropertyBuilder<T extends RentalPropertyBuilder<T>> extends PropertyBuilder<T>{
    T withMonthlyRent(int monthlyRent);
    RentalProperty build();
}
