package com.example.PropertyDemo.Property;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;



@Embeddable
public class Price {

    BigDecimal price;

    public Price() {
    }

    public Price(int price) {
        this.price = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPrice() {
        return price;
    }
}
