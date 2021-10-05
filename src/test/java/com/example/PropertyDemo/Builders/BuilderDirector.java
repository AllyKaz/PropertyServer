package com.example.PropertyDemo.Builders;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Property.PropertyType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class BuilderDirector {

    public void constructRentalProperty(RentalPropertyBuilderImpl builder, Agent agent) {
        constructProperty(builder, agent);
        builder.withMonthlyRent(1500);
    }

    public void constructAgent(AgentBuilder agentBuilder) {
        try {
            agentBuilder.withName("Frosts").withLocation(initLocation().build()).withLogo(new URL("https://logo"))
                    .withTelephoneNumber("telephoneNumber");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void constructLocation(LocationBuilder builder) {
        builder.withCity("York").withCounty("Yorkshire").withNumber(1).withPostcode("YO7 8NY")
                .withStreet("Harley Street");
    }

    public void constructSaleProperty(SalePropertyBuilderImpl salePropertyBuilder, Agent agent) {
        LocationBuilder locationBuilder = new LocationBuilder();
        constructLocation(locationBuilder);
        constructProperty(salePropertyBuilder, agent);
        salePropertyBuilder.withPrice(300000);
    }

    private static void constructProperty(PropertyBuilder<?> builder, Agent agent) {

        try {
            builder.withType(PropertyType.FLAT).withLocation(initLocation().build()).withAgent(agent)
                    .withBedrooms(3).withImages(Arrays.asList(new URL("https://url1"),
                    new URL("https://url2"), new URL("https://url3")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static SalePropertyBuilderImpl initSaleProperty(Agent agent) {
        SalePropertyBuilderImpl builder = new SalePropertyBuilderImpl();
        constructProperty(builder, agent);
        builder.withPrice(300000);
        return builder;
    }

    public static RentalPropertyBuilderImpl initRentalProperty(Agent agent) {
        RentalPropertyBuilderImpl builder = new RentalPropertyBuilderImpl();
        constructProperty(builder, agent);
        return builder.withMonthlyRent(1500);
    }

    public static LocationBuilder initLocation() {
        return new LocationBuilder().withCity("York").withCounty("Yorkshire").withNumber(1).withPostcode("YO7 8NY")
                .withStreet("Harley Street");
    }

    public static AgentBuilder initAgent() {
        AgentBuilder builder = AgentBuilder.getInstance();
        try {
            builder.withName("Frosts").withLocation(initLocation().build())
                    .withLogo(new URL("https://logo")).withTelephoneNumber("077763876543");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return builder;
    }
}
