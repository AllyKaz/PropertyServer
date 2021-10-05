package com.example.PropertyDemo;
import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Location.Location;
import com.example.PropertyDemo.Property.PropertyType;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.RentalPropertyRepository;
import com.example.PropertyDemo.Repositories.SalePropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;

@Component
@Profile("dev")
public class DatabasePopulation implements CommandLineRunner {

    @Autowired
    RentalPropertyRepository rentalPropertyRepository;

    @Autowired
    SalePropertyRepository salePropertyRepository;

    @Autowired
    AgentRepository agentRepository;


    public void run(String... args) throws Exception {
        Agent agent = new Agent("Frosts",
                new Location(8, "Paul Street", "Liverpool", "Linconshire", "LP3 48S"),
                "07789 786543",
                new URL("http://www.test.com/agent_logo.jpg"));

        ArrayList<URL> propertyImages = new ArrayList<>();
        propertyImages.add(new URL("http://www.test.com/property_image1.jpg"));
        propertyImages.add(new URL("http://www.test.com/property_image2.jpg"));
        propertyImages.add(new URL("http://www.test.com/property_image3.jpg"));

        agentRepository.save(agent);

        salePropertyRepository.save(new SaleProperty(
                PropertyType.FLAT,
                new Location(1, "Davey Street", "London", "Greater London", "NW7 7AE"),
                1,
                propertyImages,
                agent,
                350000)
        );

        rentalPropertyRepository.save(new RentalProperty(
                PropertyType.HOUSE_DETACHED,
                new Location(2, "london road", "St Albans", "Hertfordshire",
                        "HL1 74A"),
                5,
                propertyImages,
                agent,
                1500)
        );

        salePropertyRepository.save(new SaleProperty(
                PropertyType.FLAT,
                new Location(73, "Mews Lane", "Manchester", "Greater Manchester",
                        "MC1 73P"),
                2,
                propertyImages,
                agent,
                300000)
        );

        rentalPropertyRepository.save(new RentalProperty(
                PropertyType.FLAT,
                new Location(103, "Black Cut", "Bristol", "Bristol", "BR7 6PE"),
                3,
                propertyImages,
                agent,
                850)
        );

        salePropertyRepository.save(new SaleProperty(
                PropertyType.HOUSE_TERRACED,
                new Location(37, "Harley Street", "Brighton", "East Sussex", "BR7 19E"),
                6,
                propertyImages,
                agent,
                1500000)
        );
    }
}
