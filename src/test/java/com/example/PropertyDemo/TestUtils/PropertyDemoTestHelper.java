package com.example.PropertyDemo.TestUtils;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Location.Location;
import com.example.PropertyDemo.Property.PropertyType;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public abstract class PropertyDemoTestHelper {


    private static List<String> streets = Arrays.asList("Ovaltine Drive", "Harley Street", "Baker Street", "Downing Street");
    private static List<String> cities = Arrays.asList("London", "Sheffield", "Manchester", "Leeds");
    private static List<String> counties = Arrays.asList("Hertfordshire", "Yorkshire", "Cambridgeshire", "Norfolk");
    private static List<String> postCodes = Arrays.asList("WD48GY", "Al179R", "HP18TP", "PQ397T");
    private static List<PropertyType> propertyTypes = Arrays.asList(PropertyType.values());

    private static Random r =  new Random();

    public static RentalProperty generateRentalProperty(Agent agent) throws MalformedURLException {
        RentalProperty property = new RentalProperty(propertyTypes.get(r.nextInt(propertyTypes.size())),
                generateLocation(), r.nextInt(10),
                Arrays.asList(new URL("https://url" + r.nextInt()), new URL("https://url" + r.nextInt()),
                        new URL("https://" + r.nextInt())), agent, 1000 + r.nextInt(1000));
        property.setId(generateId());
        agent.addProperty(property);
        return property;
    }

    public static SaleProperty generateSaleProperty(Agent agent) throws MalformedURLException {
        SaleProperty property = new SaleProperty(propertyTypes.get(r.nextInt(propertyTypes.size())),
                generateLocation(), r.nextInt(10),
                Arrays.asList(generateURL(), generateURL(), generateURL()), agent, 200000 + r.nextInt());
        property.setId(generateId());
        agent.addProperty(property);
        return property;
    }

    public static URL generateURL() throws MalformedURLException {
        return new URL("https://url" + r.nextInt());
    }

    public static Location generateLocation() {
        return new Location(r.nextInt(500), streets.get(r.nextInt(streets.size())),
                cities.get(r.nextInt(cities.size())), counties.get(r.nextInt(counties.size())),
                postCodes.get(r.nextInt(postCodes.size())));
    }

    public static Agent generateAgent() throws MalformedURLException {
        int id = generateId();
        Agent agent = new Agent("Agent " + id, generateLocation(), "" + r.nextInt(),
                new URL("https://logo" + id));
        agent.setId(id);
        return agent;
    }

    private static int idCount = 0;

    public static int generateId() {
        return idCount++;
    }

    public static List<MockMultipartFile> createImageMultipart(int size) {
        List<MockMultipartFile> files = new ArrayList<>();
        IntStream.range(0, size).forEach(i -> {
            String filename = "test_image_" + (i + 1) + ".jpeg";
            try {
                Path path = new ClassPathResource(filename).getFile().toPath();
                files.add(new MockMultipartFile("images", filename, "image/jpg",
                        Files.readAllBytes(path)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return files;
    }

    public static RentalProperty copyRentalProperty(RentalProperty property) {
        return  new RentalProperty(property.getType(), copyLocation(property.getLocation()), property.getBedrooms(),
                property.getImages(), property.getAgent(), property.getMonthlyRent());
    }

    public static Location copyLocation(Location location) {
        return new Location(location.getNumber(), location.getStreet(), location.getCity(), location.getCounty(),
                location.getPostCode());
    }
}
