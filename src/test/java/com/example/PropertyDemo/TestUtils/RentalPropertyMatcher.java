package com.example.PropertyDemo.TestUtils;

import com.example.PropertyDemo.Property.RentalProperty;
import org.mockito.ArgumentMatcher;

public class RentalPropertyMatcher implements ArgumentMatcher<RentalProperty> {

    RentalProperty expected;

    public RentalPropertyMatcher(RentalProperty expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(RentalProperty actual) {
        boolean result = expected.getId() == actual.getId()
                && expected.getLocation().equals(actual.getLocation())
                && expected.getType().equals(actual.getType())
                && expected.getImages().equals(actual.getImages())
                && expected.getBedrooms() == actual.getBedrooms()
                && expected.getMonthlyRent() == actual.getMonthlyRent();
        return result;
    }
}
