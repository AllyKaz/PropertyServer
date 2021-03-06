package com.example.PropertyServer.Builders;

import com.example.PropertyServer.Property.RentalProperty;

public class RentalPropertyBuilderImpl extends PropertyBuilderAdapter<RentalPropertyBuilderImpl>
        implements RentalPropertyBuilder<RentalPropertyBuilderImpl> {

    public int monthlyRent;

    @Override
    public RentalPropertyBuilderImpl withMonthlyRent(int monthlyRent) {
        this.monthlyRent = monthlyRent;
        return this;
    }

    public RentalProperty build() {
        return new RentalProperty(type, location,bedrooms, images, agent, monthlyRent);
    }
}
