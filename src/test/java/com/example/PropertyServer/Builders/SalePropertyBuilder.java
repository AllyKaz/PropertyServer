package com.example.PropertyServer.Builders;

import com.example.PropertyServer.Property.SaleProperty;

public interface SalePropertyBuilder<T extends SalePropertyBuilder<T>> extends PropertyBuilder<T> {

    public T withPrice(int price);
    public SaleProperty build();

}
