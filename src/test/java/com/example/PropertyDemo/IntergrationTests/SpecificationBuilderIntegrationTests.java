package com.example.PropertyDemo.IntergrationTests;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Config.BeansConfig;
import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.PropertyType;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.PropertyBaseRepository;
import com.example.PropertyDemo.Repositories.RentalPropertyRepository;
import com.example.PropertyDemo.Repositories.SalePropertyRepository;
import com.example.PropertyDemo.SpecificationBuilders.RentalPropertySpecificationBuilder;
import com.example.PropertyDemo.SpecificationBuilders.SalePropertySpecificationBuilder;
import com.example.PropertyDemo.SpecificationBuilders.SpecificationBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.PropertyDemo.Builders.BuilderDirector.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ImportAutoConfiguration(BeansConfig.class)
public class SpecificationBuilderIntegrationTests {

    @Autowired
    PropertyBaseRepository<Property> propertyBaseRepository;

    @Autowired
    AgentRepository agentRepository;

    @Autowired
    RentalPropertyRepository rentalPropertyRepository;

    @Autowired
    SalePropertyRepository salePropertyRepository;

    @Autowired
    SpecificationBuilder<Property> specificationBuilder;

    @Autowired
    RentalPropertySpecificationBuilder rentalPropertySpecificationBuilder;

    @Autowired
    SalePropertySpecificationBuilder salePropertySpecificationBuilder;


    @Test
    public void findAllProperties() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());

        List<Property> list = propertyBaseRepository.findAll(specificationBuilder.build(Collections.emptyMap()));
        assertThat(list.size(), equalTo(3));
    }

    @Test
    public void findAllRentalProperties() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());

        List<RentalProperty> list = rentalPropertyRepository.findAll(
                rentalPropertySpecificationBuilder.build(Collections.emptyMap()));
        assertThat(list.size(), equalTo(2));
    }

    @Test
    public void findAllSaleProperties() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());

        List<SaleProperty> list = salePropertyRepository.findAll(salePropertySpecificationBuilder.build(Collections.emptyMap()));
        assertThat(list.size(), equalTo(2));
    }

    @Test
    public void findByCity() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withLocation(
                initLocation().withCity("Manchester").build()).build());

        Map<String, String> params = Map.of("city", "York");
        List<Property> list = propertyBaseRepository.findAll(specificationBuilder.build(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getLocation().getCity(), equalTo(params.get("city")));
    }

    @Test
    public void findByPostCode() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withLocation(
                initLocation().withPostcode("MC1 79P").build()).build());

        Map<String, String> params = Map.of("postcode", "YO");
        List<Property> list = propertyBaseRepository.findAll(specificationBuilder.build(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getLocation().getPostCode(), startsWith(params.get("postcode")));
    }

    @Test
    public void findByType() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());

        Map<String, String> params = Map.of("type", "FLAT");
        List<Property> list = propertyBaseRepository.findAll(specificationBuilder.build(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getType().toString(), equalTo(params.get("type")));
    }

    @Test
    void findByMinBedrooms() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(1).build());

        Map<String, String> params = Map.of("min", "2");
        List<Property> list = propertyBaseRepository.findAll(specificationBuilder.build(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getBedrooms(), greaterThanOrEqualTo(Integer.parseInt(params.get("min"))));
    }

    @Test
    public void findByMaxBedrooms() {
        Agent agent = agentRepository.save(initAgent().build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(5).build());

        Map<String, String> params = Map.of("max", "4");
        List<Property> list = propertyBaseRepository.findAll(specificationBuilder.build(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getBedrooms(), lessThanOrEqualTo(Integer.parseInt(params.get("max"))));


    }

    @Test
    public void findByCityAndTypeAndMinAndMaxBedrooms() {
        Agent agent = agentRepository.save(initAgent().build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent)
                .withLocation(initLocation().withCity("Manchester").build()).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(1).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(4).build());

        Map<String, String> params = Map.of("city", "York", "type", "FLAT",
                "min", "2", "max", "3");
        List<Property> list = propertyBaseRepository.findAll(specificationBuilder.build(params));
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
                .withLocation(initLocation().withPostcode("MC7 9PQ").build()).build());
        salePropertyRepository.save(initSaleProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(1).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(4).build());

        Map<String, String> params = Map.of("postcode", "YO", "type", "FLAT",
                "min", "2", "max", "3");
        List<Property> list = propertyBaseRepository.findAll(specificationBuilder.build(params));

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
                initLocation().withPostcode("MC7 8PQ").build()).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(1).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(5).build());


        Map<String, String> params = Map.of("postcode", "YO", "type", "FLAT",
                "min", "2", "max", "4");
        List<RentalProperty> list = rentalPropertyRepository.findAll(rentalPropertySpecificationBuilder.build(params));
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
                initLocation().withCity("Manchester").build()).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(1).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(5).build());

        Map<String, String> params = Map.of("city", "York", "type", "FLAT",
                "min", "2", "max", "4");
        List<RentalProperty> list = rentalPropertyRepository.findAll(rentalPropertySpecificationBuilder.build(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getLocation().getCity(), equalTo(params.get("city")));
        assertThat(list.get(0).getType(), equalTo(PropertyType.valueOf(params.get("type"))));
        assertThat(list.get(0).getBedrooms(), greaterThanOrEqualTo(Integer.parseInt(params.get("min"))));
        assertThat(list.get(0).getBedrooms(), lessThanOrEqualTo(Integer.parseInt(params.get("max"))));

    }

    @Test
    public void findSalesByCityAndTypeAndMinAndMaxBedroomsAndMinAndMaxPrice() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).build());
        salePropertyRepository.save(initSaleProperty(agent).withLocation(
                initLocation().withCity("Manchester").build()).build());
        salePropertyRepository.save(initSaleProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(1).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(5).build());
        salePropertyRepository.save(initSaleProperty(agent).withPrice(200000).build());
        salePropertyRepository.save(initSaleProperty(agent).withPrice(400000).build());

        Map<String, String> params = Map.of("city", "York", "type", "FLAT",
                "min", "2", "max", "4", "minPrice", "250000", "maxPrice", "350000");
        List<SaleProperty> list = salePropertyRepository.findAll(salePropertySpecificationBuilder.build(params));
        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getLocation().getCity(), equalTo(params.get("city")));
        assertThat(list.get(0).getType(), equalTo(PropertyType.valueOf(params.get("type"))));
        assertThat(list.get(0).getBedrooms(), is(both(greaterThanOrEqualTo(Integer.parseInt(params.get("min"))))
                .and(lessThanOrEqualTo(Integer.parseInt(params.get("max"))))));
        assertThat(list.get(0).getPrice(), is(both(greaterThanOrEqualTo(Integer.parseInt(params.get("minPrice"))))
                .and(lessThanOrEqualTo(Integer.parseInt(params.get("maxPrice"))))));
    }

    @Test
    public void nonExistentTypeThrows_InvalidDataAccessApiUsageException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            List<Property> list = propertyBaseRepository.findAll(
                    specificationBuilder.build(Map.of("type", "castle")));
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
                specificationBuilder.build(Collections.emptyMap()));
        assertThat(list.size(), equalTo(4));
    }

    @Test
    public void unsupportedField() {
        assertThrows(IllegalArgumentException.class, () -> {
            propertyBaseRepository.findAll(specificationBuilder.build(Map.of("wheels", "4")));
        });
    }

    @Test
    public void findByMinMonthlyRent() {
        Agent agent = agentRepository.save(initAgent().build());
        rentalPropertyRepository.save(initRentalProperty(agent).withMonthlyRent(1000).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withMonthlyRent(2000).build());

        List<RentalProperty> properties = rentalPropertyRepository.findAll(rentalPropertySpecificationBuilder
                .build(Map.of("minMonthlyRent", "1500")));

        assertThat(properties.size(), equalTo(1));
    }

    @Test
    public void findByMaxMonthlyRent() {
        Agent agent = agentRepository.save(initAgent().build());
        rentalPropertyRepository.save(initRentalProperty(agent).withMonthlyRent(1000).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withMonthlyRent(2000).build());

        List<RentalProperty> properties = rentalPropertyRepository.findAll(rentalPropertySpecificationBuilder
            .build(Map.of("maxMonthlyRent", "1500")));

        assertThat(properties.size(), equalTo(1));
    }

    @Test
    public void findByCityAndPostCodeAndPropertyTypeAndMinAndMaxBedroomsAndMinAndMaxMonthlyRent() {
        Agent agent = agentRepository.save(initAgent().build());
        rentalPropertyRepository.save(initRentalProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent)
                .withLocation(initLocation().withCity("Manchester").build()).build());
        rentalPropertyRepository.save(initRentalProperty(agent)
                .withLocation(initLocation().withPostcode("MC7 9AP").build()).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(1).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(5).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withMonthlyRent(9000).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withMonthlyRent(2500).build());

        List<RentalProperty> properties = rentalPropertyRepository.findAll(rentalPropertySpecificationBuilder
        .build(Map.of("city", "York", "postcode", "YO", "type", "FLAT",
                "min", "2", "max", "4",
                "minMonthlyRent", "1000", "maxMonthlyRent", "2000")));
        assertThat(properties.size(), equalTo(1));

    }

    @Test
    public void findByMinPrice() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).withPrice(300000).build());
        salePropertyRepository.save(initSaleProperty(agent).withPrice(750000).build());

        List<SaleProperty> properties = salePropertyRepository.findAll(salePropertySpecificationBuilder
                .build(Map.of("minPrice", "500000")));

        assertThat(properties.size(), equalTo(1));
    }

    @Test
    public void findByMaxPrice() {
        Agent agent = agentRepository.save(initAgent().build());
        salePropertyRepository.save(initSaleProperty(agent).withPrice(300000).build());
        salePropertyRepository.save(initSaleProperty(agent).withPrice(750000).build());

        List<SaleProperty> properties = salePropertyRepository.findAll(salePropertySpecificationBuilder
                .build(Map.of("maxPrice", "500000")));

        assertThat(properties.size(), equalTo(1));

    }

}
