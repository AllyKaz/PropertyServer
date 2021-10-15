package com.example.PropertyServer.Builders;

import com.example.PropertyServer.Property.RentalProperty;

public interface RentalPropertyBuilder<T extends RentalPropertyBuilder<T>> extends PropertyBuilder<T>{
    T withMonthlyRent(int monthlyRent);
    RentalProperty build();
}
