package com.example.PropertyServer.IntergrationTests;

import com.example.PropertyServer.Agent.Agent;
import com.example.PropertyServer.Location.Location;
import com.example.PropertyServer.Property.Property;
import com.example.PropertyServer.Property.PropertyType;
import com.example.PropertyServer.Property.RentalProperty;
import com.example.PropertyServer.Property.SaleProperty;
import com.example.PropertyServer.Repositories.AgentRepository;
import com.example.PropertyServer.Repositories.PropertyBaseRepository;
import com.example.PropertyServer.Repositories.RentalPropertyRepository;
import com.example.PropertyServer.Repositories.SalePropertyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.ServletContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.PropertyServer.Builders.BuilderDirector.*;
import static com.example.PropertyServer.TestUtils.TestUtils.*;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PropertyControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropertyBaseRepository<Property> propertyRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private RentalPropertyRepository rentalPropertyRepository;

    @Autowired
    private SalePropertyRepository salePropertyRepository;

    @Autowired
    private ServletContext context;




    ObjectMapper mapper = new ObjectMapper();

    private final String USERNAME;
    private final String PASSWORD;

    private final String S3_BUCKET_NAME;

    final String AMAZON_S3_BUCKET_URL_REGEX;

    public PropertyControllerIntegrationTests(@Value("${spring.security.user.name}") String username,
            @Value("${spring.security.user.password}") String password, @Value("${s3.bucket.name}") String bucketname) {
        this.USERNAME = username;
        this.PASSWORD = password;
        this.S3_BUCKET_NAME = bucketname;
        this.AMAZON_S3_BUCKET_URL_REGEX = "https://" + S3_BUCKET_NAME + "\\.s3\\.eu-west-2\\.amazonaws\\.com/";

    }

    @Test
    public void getAllProperties() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        RentalProperty rentalProperty = rentalPropertyRepository.save(initRentalProperty(agent).build());
        SaleProperty   saleProperty = salePropertyRepository.save(initSaleProperty(agent).build());

        mockMvc.perform(get("/properties"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(rentalProperty, saleProperty))));
    }

    @Test
    void getOnlyRentals() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        RentalProperty rentalProperty = rentalPropertyRepository.save(initRentalProperty(agent).build());
        SaleProperty saleProperty = salePropertyRepository.save(initSaleProperty(agent).build());

        mockMvc.perform(get("/rentalProperties").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(rentalProperty))));
    }

    @Test
    void getOnlySales() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        RentalProperty rentalProperty = rentalPropertyRepository.save(initRentalProperty(agent).build());
        SaleProperty saleProperty = salePropertyRepository.save(initSaleProperty(agent).build());

        mockMvc.perform(get("/saleProperties").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(saleProperty))));
    }

    @Test
    public void getAllAgents() throws Exception {
        Agent first_agent = agentRepository.save(initAgent().build());
        Agent second_agent = agentRepository.save(initAgent().build());

        mockMvc.perform(get("/agents").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(first_agent, second_agent))));
    }

    @Test
    public void getAgentForProperty() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        RentalProperty rentalProperty = rentalPropertyRepository.save(initRentalProperty(agent).build());

        mockMvc.perform(get("/properties/" + rentalProperty.getId() + "/agent").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                .andExpect(content().json(mapper.writeValueAsString(agent)));
    }

    @Test
    public void getAgentProperties() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        RentalProperty rentalProperty = rentalPropertyRepository.save(initRentalProperty(agent).build());
        SaleProperty saleProperty = salePropertyRepository.save(initSaleProperty(agent).build());
        agent.addProperty(rentalProperty);
        agent.addProperty(saleProperty);
        agent = agentRepository.save(agent);

        mockMvc.perform(get("/agents/" + agent.getId() + "/properties"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(rentalProperty, saleProperty))));
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void addRentalProperty() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        RentalProperty rentalProperty = initRentalProperty(null).withImages(Collections.emptyList()).build();
        List<MockMultipartFile> images = createImageMultipart(3);

        mockMvc.perform(multipart("/agents/" + agent.getId() + "/properties/rentals")
                .file(buildPropertyMultiPart(rentalProperty))
                .file(images.get(0)).file(images.get(1)).file(images.get(2))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.location", is(rentalProperty.getLocation()), Location.class))
                .andExpect(jsonPath("$.bedrooms", is(rentalProperty.getBedrooms())))
                .andExpect(jsonPath("$.images[0]",
                        matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_2_image_0")))
                .andExpect(jsonPath("$.images[1]",
                        matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_2_image_1")))
                .andExpect(jsonPath("$.images[2]",
                        matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_2_image_2")))
                .andExpect(jsonPath("$", hasNoJsonPath("$.agent")))
                .andExpect(jsonPath("$.monthlyRent", is(rentalProperty.getMonthlyRent())));
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void addSaleProperty() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        SaleProperty saleProperty = initSaleProperty(null).withImages(Collections.emptyList()).build();
        List<MockMultipartFile> images = createImageMultipart(3);

        mockMvc.perform(multipart("/agents/" + agent.getId() + "/properties/sales")
                .file(buildPropertyMultiPart(saleProperty))
                .file(images.get(0)).file(images.get(1)).file(images.get(2))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.location", is(saleProperty.getLocation()), Location.class))
                .andExpect(jsonPath("$.bedrooms", is(saleProperty.getBedrooms())))
                .andExpect(jsonPath("$.images[0]",
                        matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_\\d_image_0")))
                .andExpect(jsonPath("$.images[1]",
                        matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_\\d_image_1")))
                .andExpect(jsonPath("$.images[2]",
                        matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_\\d_image_2")))
                .andExpect(jsonPath("$", hasNoJsonPath("$.agent")))
                .andExpect(jsonPath("$.price", is(saleProperty.getPrice())));

    }


    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void addImagesToProperty() throws Exception {
        RentalProperty property = rentalPropertyRepository.save(
                initRentalProperty(agentRepository.save(initAgent().build())).withImages(Collections.emptyList()).build());

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart("/properties/" + property.getId() + "/images");
        builder.with(request -> { request.setMethod("PATCH"); return request;});

        List<MockMultipartFile> images = createImageMultipart(3);

        mockMvc.perform(builder
                .file(images.get(0)).file(images.get(1)).file(images.get(2))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images[0]", matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_\\d_image_0")))
                .andExpect(jsonPath("$.images[1]", matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_\\d_image_1")))
                .andExpect(jsonPath("$.images[2]", matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_\\d_image_2")));
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    void addAgent() throws Exception {
        Agent agent = initAgent().build();

        MockMultipartFile jsonFile = new MockMultipartFile("agent", "agent",
                "application/json", mapper.writeValueAsString(agent).getBytes());

        Path path = new ClassPathResource("test_image_1.jpeg").getFile().toPath();
        MockMultipartFile file = new MockMultipartFile("logo", "test_image_1.jpeg",
                "image/jpg", Files.readAllBytes(path));

        mockMvc.perform(multipart("/agents").file(file).file(jsonFile).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(agent.getName())))
                .andExpect(jsonPath("$.location", is(agent.getLocation()), Location.class))
                .andExpect(jsonPath("$.logoImage",
                        matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "agent_\\d_logo")));
    }

    @Test
    void authorisationRequiredToAddAgent() throws Exception {
        mockMvc.perform(multipart("/agents").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authorisationRequiredToAddSaleProperty() throws Exception {
        mockMvc.perform(multipart("/agents/1/properties/rentals").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authorisationRequiredToAddRentalProperty() throws Exception {
        mockMvc.perform(multipart("/agents/1/properties/sale").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authorisationRequiredToAddImagesToProperty() throws Exception {
        mockMvc.perform(multipart("/property/1/images").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPropertiesByFilters() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        RentalProperty targetProperty = rentalPropertyRepository.save(initRentalProperty(agent).build());
        salePropertyRepository.save(initSaleProperty(agent)
                .withLocation(initLocation().withCity("Manchester").build()).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(1).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(6).build());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("city", targetProperty.getLocation().getCity());
        params.add("type", targetProperty.getType().toString());
        params.add("min", Integer.toString(targetProperty.getBedrooms() - 1));
        params.add("max", Integer.toString(targetProperty.getBedrooms() + 2));

        mockMvc.perform(get("/properties").queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(targetProperty))));
    }

    @Test
    void getRentalPropertiesByFilters() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        RentalProperty targetProperty = rentalPropertyRepository.save(initRentalProperty(agent).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withLocation(initLocation()
                .withPostcode("MC7 8PQ").build()).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withLocation(initLocation()
                .withCity("Manchester").build()).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(targetProperty.getBedrooms() - 2).build());
        rentalPropertyRepository.save(initRentalProperty(agent).withBedrooms(targetProperty.getBedrooms() + 3).build());
        rentalPropertyRepository.save(initRentalProperty(agent)
                .withMonthlyRent(targetProperty.getMonthlyRent() - 1000).build());
        rentalPropertyRepository.save(initRentalProperty(agent)
                .withMonthlyRent(targetProperty.getMonthlyRent() + 1000).build());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("city", targetProperty.getLocation().getCity());
        params.add("postcode", targetProperty.getLocation().getPostCode());
        params.add("type", targetProperty.getType().toString());
        params.add("min", Integer.toString(targetProperty.getBedrooms() - 1));
        params.add("max", Integer.toString(targetProperty.getBedrooms() + 2));
        params.add("minMonthlyRent", Integer.toString(targetProperty.getMonthlyRent() - 500));
        params.add("maxMonthlyRent", Integer.toString(targetProperty.getMonthlyRent() + 500));

        mockMvc.perform(get("/rentalProperties").queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(targetProperty))));
    }

    @Test
    public void getSalesPropertiesByFilter() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        SaleProperty targetProperty = salePropertyRepository.save(initSaleProperty(agent).build());
        salePropertyRepository.save(initSaleProperty(agent).withLocation(initLocation()
                .withPostcode("MC7 9PQ").build()).build());
        salePropertyRepository.save(initSaleProperty(agent).withLocation(initLocation()
                .withCity("Manchester").build()).build());
        salePropertyRepository.save(initSaleProperty(agent).withType(PropertyType.HOUSE_TERRACED).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(targetProperty.getBedrooms() - 2).build());
        salePropertyRepository.save(initSaleProperty(agent).withBedrooms(targetProperty.getBedrooms() + 2).build());
        salePropertyRepository.save(initSaleProperty(agent).withPrice(targetProperty.getPrice() - 100000).build());
        salePropertyRepository.save(initSaleProperty(agent).withPrice(targetProperty.getPrice() + 100000).build());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("city", targetProperty.getLocation().getCity());
        params.add("postcode", targetProperty.getLocation().getPostCode());
        params.add("type", targetProperty.getType().toString());
        params.add("min", Integer.toString(targetProperty.getBedrooms() - 1));
        params.add("max", Integer.toString(targetProperty.getBedrooms() + 1));
        params.add("maxPrice", Integer.toString(targetProperty.getPrice() + 50000));
        params.add("minPrice", Integer.toString(targetProperty.getPrice() - 50000));

        mockMvc.perform(get("/saleProperties").queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(targetProperty))));
    }

    @Test
    public void getNonExistentPropertyThrowsError() throws Exception {
            mockMvc.perform(get("/properties/" + 1).accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString("errors")));
    }

    @Test
    public void getAgentForPropertyReturnsErrorForNonExistentProperty() throws Exception {
        mockMvc.perform(get("/properties/" + 1 + "/agent").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("errors")));
    }

    @Test
    public void getNonExistentAgentThrowsReturnsError() throws Exception {
        mockMvc.perform(get("/agents/" + 1).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("errors")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void invalidRentalPropertyThrowsError() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        RentalProperty rentalProperty = initRentalProperty(null).build();
        rentalProperty.setBedrooms(0);
        rentalProperty.setLocation(null);
        rentalProperty.setType(null);

        mockMvc.perform(multipart("/agents/" + agent.getId() + "/properties/rentals")
                .file(buildPropertyMultiPart(rentalProperty)).file(buildImageMultiPart()).with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("errors")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void invalidSalePropertyThrowsError() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        SaleProperty saleProperty = initSaleProperty(null).build();
        saleProperty.setBedrooms(0);
        saleProperty.setLocation(null);
        saleProperty.setType(null);

        mockMvc.perform(multipart("/agents/" + agent.getId() + "/properties/rentals")
                .file(buildPropertyMultiPart(saleProperty)).file(buildImageMultiPart()).with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void addImagesToPropertyThrowsError() throws Exception {
        int PROPERTY_ID = 1;
        mockMvc.perform(multipart("/properties/" + PROPERTY_ID + "/images")
                .file(buildImageMultiPart()).file(buildImageMultiPart()).file(buildImageMultiPart())
                .with(request -> {request.setMethod("PATCH"); return request;})
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(is(notNullValue())));
    }

    @Test
    public void addRentalPropertyWithBasicAuth() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        RentalProperty property = initRentalProperty(agent).build();

        mockMvc.perform(multipart("/agents/" + agent.getId() + "/properties/rentals")
                .file(buildPropertyMultiPart(property)).file(buildImageMultiPart())
                .with(httpBasic(USERNAME, PASSWORD))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void addSalePropertyWithBasicAuth() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        RentalProperty property = initRentalProperty(agent).build();

        mockMvc.perform(multipart("/agents/" + agent.getId() +" /properties/sales")
                .file(buildPropertyMultiPart(property)).file(buildImageMultiPart())
                .with(httpBasic(USERNAME, PASSWORD))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void addAgentWithBasicAuth() throws Exception {
        Agent agent = initAgent().build();

        mockMvc.perform(multipart("/agents")
                .file(buildAgentMultiPart(agent)).file(buildLogoMultiPart())
                .with(httpBasic(USERNAME, PASSWORD))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void addImagesToPropertyWithBasicAuth() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        Property property = rentalPropertyRepository.save(initRentalProperty(agent).build());

        mockMvc.perform(multipart("/properties/" + property.getId() + "/images")
                .file(buildImageMultiPart())
                .with(request -> {request.setMethod("PATCH"); return request;})
                .with(httpBasic(USERNAME, PASSWORD))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
