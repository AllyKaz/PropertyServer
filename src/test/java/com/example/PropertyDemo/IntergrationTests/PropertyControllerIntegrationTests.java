package com.example.PropertyDemo.IntergrationTests;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Location.Location;
import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.PropertyType;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.PropertyBaseRepository;
import com.example.PropertyDemo.Repositories.RentalPropertyRepository;
import com.example.PropertyDemo.Repositories.SalePropertyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.PropertyDemo.Builders.BuilderDirector.*;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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


    String AMAZON_S3_BUCKET_URL = "https://propertytestbucket.s3.eu-west-2.amazonaws.com/";
    String AMAZON_S3_BUCKET_URL_REGEX = "https://propertytestbucket\\.s3\\.eu-west-2\\.amazonaws\\.com/";
    String UUID_REGEX = "[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$";

    ObjectMapper mapper = new ObjectMapper();

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
        String json = mapper.writeValueAsString(rentalProperty);
        MockMultipartFile jsonMultiPart = new MockMultipartFile("property", "newProperty",
                "application/json", json.getBytes());

        List<MockMultipartFile> files = createImageMultipart(3);

        mockMvc.perform(multipart("/agents/" + agent.getId() + "/properties/rentals")
                .file(jsonMultiPart).file(files.get(0)).file(files.get(1)).file(files.get(2)).with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.location", is(rentalProperty.getLocation()), Location.class))
                .andExpect(jsonPath("$.bedrooms", is(rentalProperty.getBedrooms())))
                .andExpect(jsonPath("$.images[0]", matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_2_image_0")))
                .andExpect(jsonPath("$.images[1]", matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_2_image_1")))
                .andExpect(jsonPath("$.images[2]", matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_2_image_2")))
                .andExpect(jsonPath("$", hasNoJsonPath("$.agent")))
                .andExpect(jsonPath("$.monthlyRent", is(rentalProperty.getMonthlyRent())));
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void addSaleProperty() throws Exception {
        Agent agent = agentRepository.save(initAgent().build());
        SaleProperty saleProperty = salePropertyRepository.save(initSaleProperty(null)
                .withImages(Collections.emptyList()).build());
        MockMultipartFile jsonMultiPart = new MockMultipartFile("property", "newProperty",
                "application/json", mapper.writeValueAsString(saleProperty).getBytes());

        List<MockMultipartFile> files = createImageMultipart(3);

        mockMvc.perform(multipart("/agents/" + agent.getId() + "/properties/sales")
                .file(jsonMultiPart).file(files.get(0)).file(files.get(1)).file(files.get(2)).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.location", is(saleProperty.getLocation()), Location.class))
                .andExpect(jsonPath("$.bedrooms", is(saleProperty.getBedrooms())))
                .andExpect(jsonPath("$.images[0]", matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_\\d_image_0")))
                .andExpect(jsonPath("$.images[1]", matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_\\d_image_1")))
                .andExpect(jsonPath("$.images[2]", matchesPattern(AMAZON_S3_BUCKET_URL_REGEX + "property_\\d_image_2")))
                .andExpect(jsonPath("$", hasNoJsonPath("$.agent")))
                .andExpect(jsonPath("$.price", is(saleProperty.getPrice())));

    }

    private List<MockMultipartFile> createImageMultipart(int size) {
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

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void addImagesToProperty() throws Exception {
        RentalProperty property = rentalPropertyRepository.save(
                initRentalProperty(agentRepository.save(initAgent().build())).withImages(Collections.emptyList()).build());
        List<MockMultipartFile> files = createImageMultipart(3);
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart("/properties/" + property.getId() + "/images");
        builder.with(request -> { request.setMethod("PATCH"); return request;});

        mockMvc.perform(builder
                .file(files.get(0)).file(files.get(1)).file(files.get(2)).with(csrf()))
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
                .andExpect(status().isForbidden());
    }

    @Test
    void authorisationRequiredToAddSaleProperty() throws Exception {
        mockMvc.perform(multipart("/agents/1/properties/rentals").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void authorisationRequiredToAddRentalProperty() throws Exception {
        mockMvc.perform(multipart("/agents/1/properties/sale").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void authorisationRequiredToAddImagesToProperty() throws Exception {
        mockMvc.perform(multipart("/property/1/images").with(csrf()))
                .andExpect(status().isForbidden());
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


    /*
    @Disabled
    @ParameterizedTest
    @MethodSource("searchParams")
    public void testRentalSearch(String city, String postCode, String type, String min, String max, String url) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if(city != null) params.put("city", Collections.singletonList(city));
        if(postCode != null) params.put("postCode", Collections.singletonList(postCode));
        if(type != null) params.put("type", Collections.singletonList(type.toString()));
        if(min != null) params.put("min", Collections.singletonList(min.toString()));
        if(max != null) params.put("max", Collections.singletonList(max.toString()));

        MvcResult result = mockMvc.perform(get(url).queryParams(params)).andReturn();
        String json = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode propertiesNode = mapper.readTree(json);

        propertiesNode.forEach(property -> {
            if(city != null) assertThat(property.get("location").get("city").asText(), is(equalToIgnoringCase(city)));
            if(postCode != null) assertThat(property.get("location").get("postCode").asText(),
                    containsStringIgnoringCase(postCode));
            if(type != null) assertThat(property.get("type").asText(), is(equalToIgnoringCase(type)));
            if(min != null) assertThat( property.get("bedrooms").asInt(), is(greaterThanOrEqualTo(Integer.parseInt(min))));
            if(max != null) assertThat(property.get("bedrooms").asInt(), is(lessThanOrEqualTo(Integer.parseInt(max))));
        });
    }

    private static Stream<Arguments> searchParams() {
        List<List<String>> result = new ArrayList<>();
        Stream<Arguments> finalArguments = Stream.empty();
        generateArguments(5, result, new String[5], 0);
        for(String url : Arrays.asList("/properties", "/rentalProperties", "/saleProperties")) {
            Stream<Arguments> arguments = result.stream().map(list -> Arguments.of(list.get(0), list.get(1),
                    list.get(2), list.get(3), list.get(4), url));
            finalArguments = Stream.of(finalArguments, arguments).flatMap(s -> s);
        }
        return finalArguments;
    }

    private static List<String> argNames = Arrays.asList("Test city", "Test Postcode", "FLAT", "2", "4");

    private static void generateArguments(int n, List<List<String>> result,
                                          String[] temp, int i) {
        if(i == n) {
            result.add(new ArrayList<>(Arrays.asList(temp)));
            return;
        }

        temp[i] = argNames.get(i);
        generateArguments(n, result, temp, i + 1);

        temp[i] =  null;
        generateArguments(n, result, temp, i + 1);

    }

    @Disabled
    @Nested
    class search {

        @Test
        public void filterByCity() throws Exception {
            mockMvc.perform(get("/properties").queryParam("city", "Test City"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].location.city", is(equalTo("Test City"))));
        }

        @Test
        public void filterByPostCode() throws Exception {
            mockMvc.perform(get("/properties").queryParam("postCode", "Test Postcode"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].location.postCode", is(equalTo("Test Postcode"))));
        }

        @Test
        public void filterByType() throws Exception {
            mockMvc.perform(get("/properties").queryParam("type", "FLAT"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].type", is(equalTo("FLAT"))));
        }

        @Test
        public void filterByMinBeds() throws Exception {
            mockMvc.perform(get("/properties").queryParam("min", "4"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].bedrooms", is(greaterThanOrEqualTo(4))));
        }

        @Test
        public void filterByMaxBeds() throws Exception {
            mockMvc.perform(get("/properties").queryParam("max", "4"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].bedrooms", is(lessThanOrEqualTo(4))));

        }


        @Test
        public void filterByCityAndMinBeds() throws Exception {
            mockMvc.perform(get("/properties")
                    .queryParam("city", "Test City")
                    .queryParam("min", "4"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].location.city", is(equalTo("Test City"))))
                    .andExpect(jsonPath("[0].bedrooms", is(greaterThanOrEqualTo(4))));
        }

        @Test
        public void filterByCityAndMaxBeds() throws Exception {
            mockMvc.perform(get("/properties")
                    .queryParam("city", "Test City 2")
                    .queryParam("max", "4"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].location.city", is(equalTo("Test City 2"))))
                    .andExpect(jsonPath("$[0].bedrooms", is(lessThanOrEqualTo(4))));
        }

        @Test
        public void filterByTypeAndMaxBeds() throws Exception {
            mockMvc.perform(get("/properties")
                    .queryParam("type", "FLAT")
                    .queryParam("max", "4"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].type", is(equalTo("FLAT"))))
                    .andExpect(jsonPath("$[0].bedrooms", is(lessThanOrEqualTo(4))));
        }

        @Test
        public void filterByTypeAndMinBeds() throws Exception {
            mockMvc.perform(get("/properties")
                    .queryParam("type", "HOUSE_TERRACED")
                    .queryParam("min", "4"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].type", is(equalTo("HOUSE_TERRACED"))))
                    .andExpect(jsonPath("$[0].bedrooms", is(greaterThanOrEqualTo(4))));
        }

        @Test
        public void filterByCityAndType() throws Exception {
            mockMvc.perform(get("/properties")
                    .queryParam("city", "Test City")
                    .queryParam("type", "HOUSE_TERRACED"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].location.city", is(equalTo("Test City"))))
                    .andExpect(jsonPath("$[0].type", is(equalTo("HOUSE_TERRACED"))));
        }

        @Test
        public void filterByCityAndTypeAndMaxBeds() throws Exception {
            mockMvc.perform(get("/properties")
                    .queryParam("city", "Test City 2")
                    .queryParam("type", "FLAT")
                    .queryParam("max", "4"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].location.city", is(equalTo("Test City 2"))))
                    .andExpect(jsonPath("$[0].type", is(equalTo("FLAT"))))
                    .andExpect(jsonPath("$[0].bedrooms", is(lessThanOrEqualTo(4))));
        }

        @Test
        public void filterByCityAndTypeAndMinBeds() throws Exception {
            mockMvc.perform(get("/properties")
                    .queryParam("city", "Test City")
                    .queryParam("type", "HOUSE_TERRACED")
                    .queryParam("min", "4"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].location.city", is(equalTo("Test City"))))
                    .andExpect(jsonPath("$[0].type", is(equalTo("HOUSE_TERRACED"))))
                    .andExpect(jsonPath("$[0].bedrooms", is(greaterThanOrEqualTo(4))));
        }
    }*/







}
