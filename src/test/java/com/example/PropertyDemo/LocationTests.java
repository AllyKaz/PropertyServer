package com.example.PropertyDemo;

import com.example.PropertyDemo.Location.Location;
import com.example.PropertyDemo.Property.RentalProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

import static com.example.PropertyDemo.Builders.BuilderDirector.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class LocationTests {

    Validator validator;

    @BeforeEach
    public void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"1W1A 2AA", "SWW1 2AA", "SWA 2AA","SW1AA 2AA", "SW1A  2AA", "SW1A AAA", "SW11 2A",
            "234324", "DSFS*£1", "324FSD&*"})
    public void postCodeValidationFailsWithInvalidPostcode(String postcode) {
        Set<ConstraintViolation<Location>> violations = validator
                .validate(initLocation().withPostcode(postcode).build());
        assertThat(violations.size(), equalTo(1));

    }

    @ParameterizedTest
    @CsvFileSource(resources = "/postcodes.csv")
    public void postcodeValidationPassesWithValidPostcode(String postcode) {
        Set<ConstraintViolation<Location>> violations = validator.validate(initLocation()
                .withPostcode(postcode).build());
        assertThat(violations.size(), equalTo(0));
    }

    @Test
    public void postcodeValidationFailsWithNull() {
        Set<ConstraintViolation<Location>> violations = validator.validate(initLocation().withPostcode(null).build());
        assertThat(violations.size(), equalTo(1));
    }

    @Test
    public void validationFails() {
        Set<ConstraintViolation<Location>> violations = validator.validate(initLocation().withNumber(0)
                .withStreet(null).withPostcode(null).withCity(null).build());
        assertThat(violations.size(), equalTo(4));
        Iterator<ConstraintViolation<Location>> iterator = violations.iterator();
    }

    @Test
    public void validationPasses() {
        Set<ConstraintViolation<Location>> violations = validator.validate(initLocation().build());
        assertThat(violations.size(), equalTo(0));
    }

    @Test
    public void nestedValidation() {
        Set<ConstraintViolation<RentalProperty>> violations = validator.validate(initRentalProperty(initAgent().build())
                .withLocation(initLocation().withCity("").build()).build());
        assertThat(violations.size(), equalTo(1));
    }

}
