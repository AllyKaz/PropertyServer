package com.example.PropertyServer;

import com.example.PropertyServer.Property.Property;
import com.example.PropertyServer.Property.RentalProperty;
import com.example.PropertyServer.Property.SaleProperty;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static com.example.PropertyServer.Builders.BuilderDirector.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PropertyValidationTests {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void nullLocation() {
        Set<ConstraintViolation<Property>> violations = validator.validate(initRentalProperty(initAgent().build())
                .withLocation(null).build());
        assertThat(violations.size(), equalTo(1));
        assertThat(violations.iterator().next().getMessage(), equalTo("location must not be null"));
    }

    @Test
    public void emptyType() {
        Set<ConstraintViolation<Property>> violations = validator.validate(initRentalProperty(initAgent().build())
                .withType(null).build());
        assertThat(violations.size(), equalTo(1));
        assertThat(violations.iterator().next().getMessage(), equalTo("property type must not be null"));
    }

    @Test
    public void zeroBedrooms() {
        Set<ConstraintViolation<Property>> violations = validator.validate(initRentalProperty(initAgent().build())
                .withBedrooms(0).build());
        assertThat(violations.size(), equalTo(1));
        assertThat(violations.iterator().next().getMessage(),
                equalTo("property must have at least one bedroom"));
    }

    @Test
    public void nullLocationAndEmptyTypeAndZeroBedrooms() {
        Set<ConstraintViolation<Property>> violations = validator.validate(initRentalProperty(initAgent().build())
                .withType(null).withLocation(null).withBedrooms(0).build());
        assertThat(violations.size(), equalTo(3));
    }

    @Test
    public void rentalValidationPasses() {
        Set<ConstraintViolation<RentalProperty>> violations = validator.validate(
                initRentalProperty(initAgent().build()).build());
        assertThat(violations.size(), equalTo(0));
    }

    @Test
    public void saleValidationPasses() {
        Set<ConstraintViolation<SaleProperty>> violations = validator.validate(
                initSaleProperty(initAgent().build()).build());
    }
}
