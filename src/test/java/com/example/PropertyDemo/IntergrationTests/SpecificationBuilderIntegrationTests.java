package com.example.PropertyDemo.IntergrationTests;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.PropertyType;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.PropertyBaseRepository;
import com.example.PropertyDemo.Repositories.RentalPropertyRepository;
import com.example.PropertyDemo.Repositories.SalePropertyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.PropertyDemo.Builders.BuilderDirector.*;
import static com.example.PropertyDemo.SpecificationBuilder.buildSpecification;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class SpecificationBuilderIntegrationTests {

    @Autowired
    PropertyBaseRepository<Property> propertyBaseRepository;

    @Autowired
    AgentRepository agentRepository;

    @Autowired
    RentalPropertyRepository rentalPropertyRepository;

    @Autowired
    SalePropertyRepository salePropertyRepository;




    @Test
    public void findAllProperties() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());

        List<Property> list = propertyBaseRepository.findAll(buildSpecification(Collections.emptyMap()));
        assertThat(list.size(), equalTo(3));
    }

    @Test
    public void findAllRentalProperties() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());

        List<RentalProperty> list = rentalPropertyRepository.findAll(buildSpecification(Collections.emptyMap()));
        assertThat(list.size(), equalTo(2));
    }

    @Test
    public void findAllSaleProperties() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());

        List<SaleProperty> list = salePropertyRepository.findAll(buildSpecification(Collections.emptyMap()));
        assertThat(list.size(), equalTo(2));
    }

    @Test
    public void findByCity() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withLocation(
                constructLocation().withCity("Manchester").build()).build());

        Map<String, String> params = Map.of("city", "York");
        List<Property> list = propertyBaseRepository.findAll(buildSpecification(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getLocation().getCity(), equalTo(params.get("city")));
    }

    @Test
    public void findByPostCode() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withLocation(
                constructLocation().withPostcode("MC1 79P").build()).build());

        Map<String, String> params = Map.of("postcode", "YO");
        List<Property> list = propertyBaseRepository.findAll(buildSpecification(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getLocation().getPostCode(), startsWith(params.get("postcode")));
    }

    @Test
    public void findByType() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());

        Map<String, String> params = Map.of("type", "FLAT");
        List<Property> list = propertyBaseRepository.findAll(buildSpecification(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getType().toString(), equalTo(params.get("type")));
    }

    @Test
    void findByMinBedrooms() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(1).build());

        Map<String, String> params = Map.of("min", "2");
        List<Property> list = propertyBaseRepository.findAll(buildSpecification(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getBedrooms(), greaterThanOrEqualTo(Integer.parseInt(params.get("min"))));
    }

    @Test
    public void findByMaxBedrooms() {
        Agent agent = agentRepository.save(initAgent().build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(5).build());

        Map<String, String> params = Map.of("max", "4");
        List<Property> list = propertyBaseRepository.findAll(buildSpecification(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getBedrooms(), lessThanOrEqualTo(Integer.parseInt(params.get("max"))));


    }

    @Test
    public void findByCityAndTypeAndMinAndMaxBedrooms() {
        Agent agent = agentRepository.save(initAgent().build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent)
                .withLocation(constructLocation().withCity("Manchester").build()).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(1).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(4).build());

        Map<String, String> params = Map.of("city", "York", "type", "FLAT",
                "min", "2", "max", "3");
        List<Property> list = propertyBaseRepository.findAll(buildSpecification(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getLocation().getCity(), equalTo(params.get("city")));
        assertThat(list.get(0).getType(), equalTo(PropertyType.valueOf(params.get("type"))));
        assertThat(list.get(0).getBedrooms(), greaterThanOrEqualTo(Integer.parseInt(params.get("min"))));
        assertThat(list.get(0).getBedrooms(), lessThanOrEqualTo(Integer.parseInt(params.get("max"))));
    }

    @Test
    public void findByPostCodeAndTypeAndMinAndMaxBedrooms() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent)
                .withLocation(constructLocation().withPostcode("MC7 9PQ").build()).build());
        salePropertyRepository.save(initSaleProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(1).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(4).build());

        Map<String, String> params = Map.of("postcode", "YO", "type", "FLAT",
                "min", "2", "max", "3");
        List<Property> list = propertyBaseRepository.findAll(buildSpecification(params));

        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getLocation().getPostCode(), startsWith(params.get("postcode")));
        assertThat(list.get(0).getType(), equalTo(PropertyType.valueOf(params.get("type"))));
        assertThat(list.get(0).getBedrooms(), greaterThanOrEqualTo(Integer.parseInt(params.get("min"))));
        assertThat(list.get(0).getBedrooms(), lessThanOrEqualTo(Integer.parseInt(params.get("max"))));
    }

    @Test
    public void findRentalsByPostcodeAndTypeAndMinAndMaxBedrooms() {
        Agent agent = agentRepository.save(initAgent().build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withLocation(
                constructLocation().withPostcode("MC7 8PQ").build()).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(1).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(5).build());


        Map<String, String> params = Map.of("postcode", "YO", "type", "FLAT",
                "min", "2", "max", "4");
        List<RentalProperty> list = rentalPropertyRepository.findAll(buildSpecification(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getLocation().getPostCode(), startsWith(params.get("postcode")));
        assertThat(list.get(0).getType(), equalTo(PropertyType.valueOf(params.get("type"))));
        assertThat(list.get(0).getBedrooms(), greaterThanOrEqualTo(Integer.parseInt(params.get("min"))));
        assertThat(list.get(0).getBedrooms(), lessThanOrEqualTo(Integer.parseInt(params.get("max"))));
    }

    @Test
    public void findRentalsByCityAndTypeAndMinAndMaxBedrooms() {
        Agent agent = agentRepository.save(initAgent().build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withLocation(
                constructLocation().withCity("Manchester").build()).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(1).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(5).build());

        Map<String, String> params = Map.of("city", "York", "type", "FLAT",
                "min", "2", "max", "4");
        List<RentalProperty> list = rentalPropertyRepository.findAll(buildSpecification(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getLocation().getCity(), equalTo(params.get("city")));
        assertThat(list.get(0).getType(), equalTo(PropertyType.valueOf(params.get("type"))));
        assertThat(list.get(0).getBedrooms(), greaterThanOrEqualTo(Integer.parseInt(params.get("min"))));
        assertThat(list.get(0).getBedrooms(), lessThanOrEqualTo(Integer.parseInt(params.get("max"))));

    }

    @Test
    public void findSalesByCityAndTypeAndMinAndMaxBedrooms() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        salePropertyRepository.save(initSaleProperty(agent).withLocation(
                constructLocation().withCity("Manchester").build()).build());
        salePropertyRepository.save(initSaleProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(1).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(5).build());

        Map<String, String> params = Map.of("city", "York", "type", "FLAT",
                "min", "2", "max", "4");
        List<SaleProperty> list = salePropertyRepository.findAll(buildSpecification(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getLocation().getCity(), equalTo(params.get("city")));
        assertThat(list.get(0).getType(), equalTo(PropertyType.valueOf(params.get("type"))));
        assertThat(list.get(0).getBedrooms(), is(both(greaterThanOrEqualTo(Integer.parseInt(params.get("min"))))
                        .and(lessThanOrEqualTo(Integer.parseInt(params.get("max"))))));
    }

    @Test
    public void nonExistentTypeThrows_InvalidDataAccessApiUsageException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            List<Property> list = propertyBaseRepository.findAll(
                    buildSpecification(Map.of("type", "castle")));
        });
    }

    @Test
    public void noParametersMatchesAll() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());

        List<Property> list = propertyBaseRepository.findAll(
                buildSpecification(Collections.emptyMap()));
        assertThat(list.size(), equalTo(4));
    }

    @Test
    public void unsupportedField() {
        assertThrows(IllegalArgumentException.class, () -> {
            propertyBaseRepository.findAll(buildSpecification(Map.of("wheels", "4")));
        });
    }

}
