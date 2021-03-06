package com.example.PropertyServer.Builders;

import com.example.PropertyServer.Property.SaleProperty;

public class SalePropertyBuilderImpl extends PropertyBuilderAdapter<SalePropertyBuilderImpl>
        implements SalePropertyBuilder<SalePropertyBuilderImpl> {

    private int price;

    @Override
    public SalePropertyBuilderImpl withPrice(int price) {
        this.price = price;
        return this;
    }

    @Override
    public SaleProperty build() {
        return new SaleProperty(type, location, bedrooms, images, agent, price);
    }
}
