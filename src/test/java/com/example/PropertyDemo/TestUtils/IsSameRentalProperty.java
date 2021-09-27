package com.example.PropertyDemo.TestUtils;

import com.example.PropertyDemo.Property.RentalProperty;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsSameRentalProperty extends TypeSafeMatcher<RentalProperty> {

    private RentalProperty expectedProperty;

    public IsSameRentalProperty(RentalProperty expectedProperty) {
        this.expectedProperty = expectedProperty;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches property");
    }

    @Override
    protected boolean matchesSafely(RentalProperty actualProperty) {
        return expectedProperty.getId() == actualProperty.getId()
                && expectedProperty.getType().equals(actualProperty.getType())
                && expectedProperty.getLocation().equals(actualProperty.getLocation())
                && expectedProperty.getAgent().equals(actualProperty.getAgent())
                && expectedProperty.getImages().equals(actualProperty.getImages())
                && expectedProperty.getBedrooms() == actualProperty.getBedrooms()
                && expectedProperty.getMonthlyRent() == actualProperty.getMonthlyRent();

    }

    public static Matcher<RentalProperty> isSameRentalProperty(RentalProperty expectedProperty) {
        return new IsSameRentalProperty(expectedProperty);
    }

}
