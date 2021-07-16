package com.example.PropertyDemo;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Property.Location;
import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.PropertyType;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.PropertyBaseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PropertyControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropertyBaseRepository<Property> propertyRepository;

    @Autowired
    private AgentRepository agentRepository;


    @Test
    public void getGetRootResource() throws Exception {
        mockMvc.perform(get("/").accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/")))
                .andExpect(jsonPath("$._links.properties.href", is("http://localhost/properties{?city,postcode,type,min,max}")));

    }

    RentalProperty FIRST_PROPERTY;
    SaleProperty SECOND_PROPERTY;

    Agent FIRST_AGENT;
    Agent SECOND_AGENT;

    @BeforeEach
    private void generateObjects() throws MalformedURLException {
        FIRST_AGENT = new Agent("Test Agent", new Location(1, "Test Agent Street", "Test Agent City",
                "Test Agent County", "Test Agent Postcode"), "Test Agent Telephone",
                new URL("http://testAgentLogo"));

        FIRST_AGENT = agentRepository.save(FIRST_AGENT);

        SECOND_AGENT = new Agent("AgentName2", new Location(2, "Test Agent Street 2",
                "Test Agent City 2", "Test Agent County 2", "Test Agent Postcode 2"),
                "Test Agent Telephone 2", new URL("http://testurl2"));

        SECOND_AGENT = agentRepository.save(SECOND_AGENT);

        FIRST_PROPERTY = new RentalProperty(PropertyType.HOUSE_TERRACED,
                new Location(1, "Test Street", "Test City", "Test County", "Test Postcode" ),
                6, Arrays.asList(new URL("http://propertyImage1url"),
                new URL("http://propertyImage2url"),
                new URL("http://propertyImage3url")),
                FIRST_AGENT,
                2400);

        FIRST_PROPERTY = propertyRepository.save(FIRST_PROPERTY);

        SECOND_PROPERTY = new SaleProperty(PropertyType.FLAT,
                new Location(2, "Test Street 2", "Test City 2", "Test County 2",
                        "Test Postcode 2"), 2, Arrays.asList(
                                new URL("http://property2Image1url"),
                new URL("http://property2Image2url"), new URL("http://property2Image3url")),
                FIRST_AGENT, 1200);

        SECOND_PROPERTY = propertyRepository.save(SECOND_PROPERTY);

        FIRST_AGENT.addProperty(FIRST_PROPERTY);
        FIRST_AGENT.addProperty(SECOND_PROPERTY);

        FIRST_AGENT = agentRepository.save(FIRST_AGENT);

    }



    @SuppressWarnings("unchecked")
    @Test
    public void getAllProperties() throws Exception {

        /*given(propertyRepository.findAll(any(Specification.class))).willReturn(
                Arrays.asList(
                        FIRST_PROPERTY,
                        SECOND_PROPERTY
                )
        );*/
        String basePath = "$._embedded.rentalPropertyList";
        String basePath2 = "$._embedded.salePropertyList";
        mockMvc.perform(get("/properties").accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath(basePath + "[0].id", is(FIRST_PROPERTY.getId())))
                .andExpect(jsonPath(basePath + "[0].type", is(FIRST_PROPERTY.getType().name())))
                .andExpect(jsonPath(basePath + "[0].location.number", is(FIRST_PROPERTY.getLocation().getNumber())))
                .andExpect(jsonPath(basePath + "[0].location.street", is (FIRST_PROPERTY.getLocation().getStreet())))
                .andExpect(jsonPath(basePath + "[0].location.city", is (FIRST_PROPERTY.getLocation().getCity())))
                .andExpect(jsonPath(basePath + "[0].location.county", is(FIRST_PROPERTY.getLocation().getCounty())))
                .andExpect(jsonPath(basePath + "[0].location.postCode", is(FIRST_PROPERTY.getLocation().getPostCode())))
                .andExpect(jsonPath(basePath + "[0].bedrooms", is(FIRST_PROPERTY.getBedrooms())))
                .andExpect(jsonPath(basePath + "[0].images[0]", is(FIRST_PROPERTY.getImages().get(0).toExternalForm())))
                .andExpect(jsonPath(basePath + "[0].images[1]", is(FIRST_PROPERTY.getImages().get(1).toExternalForm())))
                .andExpect(jsonPath(basePath + "[0].images[2]", is(FIRST_PROPERTY.getImages().get(2).toExternalForm())))
                .andExpect(jsonPath(basePath, hasNoJsonPath("$[0].agent")))
                .andExpect(jsonPath(basePath + "[0]._links.self.href",
                        is("http://localhost/properties/" + FIRST_PROPERTY.getId())))
                .andExpect(jsonPath(basePath + "[0]._links.properties.href",
                        is("http://localhost/properties{?city,postcode,type,min,max}")))
                .andExpect(jsonPath(basePath2 + "[0].id", is(SECOND_PROPERTY.getId())))
                .andExpect(jsonPath(basePath2 + "[0].type", is(SECOND_PROPERTY.getType().name())))
                .andExpect(jsonPath(basePath2 + "[0].location.number", is(SECOND_PROPERTY.getLocation().getNumber())))
                .andExpect(jsonPath(basePath2 + "[0].location.street", is(SECOND_PROPERTY.getLocation().getStreet())))
                .andExpect(jsonPath(basePath2 + "[0].location.city", is(SECOND_PROPERTY.getLocation().getCity())))
                .andExpect(jsonPath(basePath2 + "[0].location.county", is(SECOND_PROPERTY.getLocation().getCounty())))
                .andExpect(jsonPath(basePath2 + "[0].location.postCode", is(SECOND_PROPERTY.getLocation().getPostCode())))
                .andExpect(jsonPath(basePath2 + "[0].bedrooms", is(SECOND_PROPERTY.getBedrooms())))
                .andExpect(jsonPath(basePath2 + "[0].images[0]", is(SECOND_PROPERTY.getImages().get(0).toExternalForm())))
                .andExpect(jsonPath(basePath2 + "[0].images[1]", is(SECOND_PROPERTY.getImages().get(1).toExternalForm())))
                .andExpect(jsonPath(basePath2 + "[0].images[2]", is(SECOND_PROPERTY.getImages().get(2).toExternalForm())))
                .andExpect(jsonPath(basePath2, hasNoJsonPath("$[0].agent")))
                .andExpect(jsonPath(basePath2 + "[0]._links.self.href",
                        is("http://localhost/properties/" + SECOND_PROPERTY.getId())))
                .andExpect(jsonPath(basePath2 + "[0]._links.properties.href",
                        is("http://localhost/properties{?city,postcode,type,min,max}")));

    }

    @Test
    public void getAgentForProperty() throws Exception {
        //given(propertyRepository.findById(1)).willReturn(Optional.of(FIRST_PROPERTY));
        mockMvc.perform(get("/properties/" + FIRST_PROPERTY.getId() + "/agent").accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(FIRST_AGENT.getId())))
                .andExpect(jsonPath("$.name", is(FIRST_AGENT.getName())))
                .andExpect(jsonPath("$.location.number", is(FIRST_AGENT.getLocation().getNumber())))
                .andExpect(jsonPath("$.location.city", is(FIRST_AGENT.getLocation().getCity())))
                .andExpect(jsonPath("$.location.county", is(FIRST_AGENT.getLocation().getCounty())))
                .andExpect(jsonPath("$.location.postCode", is(FIRST_AGENT.getLocation().getPostCode())))
                .andExpect(jsonPath("$.telephoneNumber", is(FIRST_AGENT.getTelephoneNumber())))
                .andExpect(jsonPath("$.logoImage", is(FIRST_AGENT.getLogoImage().toExternalForm())))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/agents/" + FIRST_AGENT.getId())))
                .andExpect(jsonPath("$._links.agents.href", is("http://localhost/agents")));
    }

    @Test
    public void getAllAgents() throws Exception {

        //given(agentRepository.findAll()).willReturn(Arrays.asList(FIRST_AGENT, SECOND_AGENT));

        mockMvc.perform(get("/agents").accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.agentList[0].id", is(FIRST_AGENT.getId())))
                .andExpect(jsonPath("$._embedded.agentList[0].name", is(FIRST_AGENT.getName())))
                .andExpect(jsonPath("$._embedded.agentList[0].location.number",
                        is(FIRST_AGENT.getLocation().getNumber())))
                .andExpect(jsonPath("$._embedded.agentList[0].location.street",
                        is(FIRST_AGENT.getLocation().getStreet())))
                .andExpect(jsonPath("$._embedded.agentList[0].location.city",
                        is(FIRST_AGENT.getLocation().getCity())))
                .andExpect(jsonPath("$._embedded.agentList[0].location.county",
                        is(FIRST_AGENT.getLocation().getCounty())))
                .andExpect(jsonPath("$._embedded.agentList[0].location.postCode",
                    is(FIRST_AGENT.getLocation().getPostCode())))
                .andExpect(jsonPath("$._embedded.agentList[0].telephoneNumber",
                        is(FIRST_AGENT.getTelephoneNumber())))
                .andExpect(jsonPath("$._embedded.agentList[0].logoImage",
                        is(FIRST_AGENT.getLogoImage().toExternalForm())))
                .andExpect(jsonPath("$._embedded.agentList[0]._links.self.href",
                        is("http://localhost/agents/" + FIRST_AGENT.getId())))
                .andExpect(jsonPath("$._embedded.agentList[0]._links.agents.href",
                        is("http://localhost/agents")))
                .andExpect(jsonPath("$._embedded.agentList[1].id", is(SECOND_AGENT.getId())))
                .andExpect(jsonPath("$._embedded.agentList[1].name", is(SECOND_AGENT.getName())))
                .andExpect(jsonPath("$._embedded.agentList[1].location.number",
                        is(SECOND_AGENT.getLocation().getNumber())))
                .andExpect(jsonPath("$._embedded.agentList[1].location.street",
                        is(SECOND_AGENT.getLocation().getStreet())))
                .andExpect(jsonPath("$._embedded.agentList[1].location.city",
                        is(SECOND_AGENT.getLocation().getCity())))
                .andExpect(jsonPath("$._embedded.agentList[1].location.postCode",
                        is(SECOND_AGENT.getLocation().getPostCode())))
                .andExpect(jsonPath("$._embedded.agentList[1].telephoneNumber",
                        is(SECOND_AGENT.getTelephoneNumber())))
                .andExpect(jsonPath("$._embedded.agentList[1].logoImage",
                        is(SECOND_AGENT.getLogoImage().toExternalForm())))
                .andExpect(jsonPath("$._embedded.agentList[1]._links.self.href",
                        is("http://localhost/agents/" + SECOND_AGENT.getId())))
                .andExpect(jsonPath("$._embedded.agentList[1]._links.agents.href",
                        is("http://localhost/agents")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/agents")));
    }
    @Test
    public void getAgentProperties() throws Exception {
        mockMvc.perform(get("/agents/" + FIRST_AGENT.getId() + "/properties"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].id", is(FIRST_PROPERTY.getId())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].type", is(FIRST_PROPERTY.getType().name())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].location.number", is(FIRST_PROPERTY.getLocation().getNumber())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].location.street", is(FIRST_PROPERTY.getLocation().getStreet())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].location.city", is(FIRST_PROPERTY.getLocation().getCity())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].location.county", is(FIRST_PROPERTY.getLocation().getCounty())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].location.postCode", is(FIRST_PROPERTY.getLocation().getPostCode())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].bedrooms", is(FIRST_PROPERTY.getBedrooms())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].images[0]", is(FIRST_PROPERTY.getImages().get(0).toExternalForm())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].images[1]", is(FIRST_PROPERTY.getImages().get(1).toExternalForm())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].images[2]", is(FIRST_PROPERTY.getImages().get(2).toExternalForm())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0].monthlyRent", is(FIRST_PROPERTY.getMonthlyRent())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0]._links.self.href", is("http://localhost/properties/" + FIRST_PROPERTY.getId())))
                .andExpect(jsonPath("$._embedded.rentalPropertyList[0]._links.properties.href", is("http://localhost/properties{?city,postcode,type,min,max}")))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].id", is(SECOND_PROPERTY.getId())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].type", is(SECOND_PROPERTY.getType().name())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].location.number", is(SECOND_PROPERTY.getLocation().getNumber())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].location.street", is(SECOND_PROPERTY.getLocation().getStreet())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].location.city", is(SECOND_PROPERTY.getLocation().getCity())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].location.county", is(SECOND_PROPERTY.getLocation().getCounty())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].location.postCode", is(SECOND_PROPERTY.getLocation().getPostCode())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].bedrooms", is(SECOND_PROPERTY.getBedrooms())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].images[0]", is(SECOND_PROPERTY.getImages().get(0).toExternalForm())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].images[1]", is(SECOND_PROPERTY.getImages().get(1).toExternalForm())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].images[2]", is(SECOND_PROPERTY.getImages().get(2).toExternalForm())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0].price", is(SECOND_PROPERTY.getPrice())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0]._links.self.href", is("http://localhost/properties/" + SECOND_PROPERTY.getId())))
                .andExpect(jsonPath("$._embedded.salePropertyList[0]._links.properties.href", is("http://localhost/properties{?city,postcode,type,min,max}")));

    }

    @Test
    public void addProperty() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Agent agent = new Agent("Agent Name", new Location(1, "Agent Street", "Agent City",
                "Agent County", "Agent Postcode"), "Agent Telephone",
                new URL("http://agentlogo"));
        RentalProperty property = new RentalProperty(PropertyType.HOUSE_TERRACED, new Location(1,
                "Test Street", "Test City", "Test County","Test PostCode"), 3,
                Arrays.asList(new URL("http://imageurl1"), new URL("http://imageurl2"),
                        new URL("http://imageurl3")), agent, 2300);
        String json = objectMapper.writeValueAsString(property);

        mockMvc.perform(post("/properties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(header().string("location", matchesPattern("http://localhost/properties/\\d")));


        // end::[]

    }

    @Test
    public void canGetNewleyCreatedProperty() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Agent agent = new Agent("Agent Name", new Location(1, "Agent Street", "Agent City",
                "Agent County", "Agent Postcode"), "Agent Telephone",
                new URL("http://agentlogo"));
        RentalProperty property = new RentalProperty(PropertyType.HOUSE_TERRACED, new Location(1,
                "Test Street", "Test City", "Test County","Test PostCode"), 3,
                Arrays.asList(new URL("http://imageurl1"), new URL("http://imageurl2"),
                        new URL("http://imageurl3")), FIRST_AGENT, 2300);

        String json = objectMapper.writeValueAsString(property);

         MvcResult result = mockMvc.perform(post("/properties").contentType(MediaType.APPLICATION_JSON)
                 .content(json)).andReturn();
         String[] url = result.getResponse().getHeader("location").split("/");
         String id = url[url.length-1];

          mockMvc.perform(get("/properties/" + id)
                  .accept(MediaTypes.HAL_JSON_VALUE))
                  .andDo(print())
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.id", is(Integer.parseInt(id))))
                  .andExpect(jsonPath("$.location.number", is(property.getLocation().getNumber())))
                  .andExpect(jsonPath("$.location.street", is(property.getLocation().getStreet())))
                  .andExpect(jsonPath("$.location.city", is(property.getLocation().getCity())))
                  .andExpect(jsonPath("$.location.county", is(property.getLocation().getCounty())))
                  .andExpect(jsonPath("$.location.postCode", is(property.getLocation().getPostCode())))
                  .andExpect(jsonPath("$.bedrooms", is(property.getBedrooms())))
                  .andExpect(jsonPath("$.images[0]", is(property.getImages().get(0).toExternalForm())))
                  .andExpect(jsonPath("$.images[1]", is(property.getImages().get(1).toExternalForm())))
                  .andExpect(jsonPath("$.images[2]", is(property.getImages().get(2).toExternalForm())))
                  .andExpect(jsonPath("$", hasNoJsonPath("$.agent")))
                  .andExpect(jsonPath("$.monthlyRent", is((double) 2300)));

          mockMvc.perform(get("/properties/" + id + "/agent")
                  .accept(MediaTypes.HAL_JSON_VALUE))
                  .andDo(print());



    }

    @Test
    public void addPropertyToAgent() throws Exception {
        RentalProperty property = new RentalProperty(PropertyType.HOUSE_TERRACED, new Location(1,
                "Test Street", "Test City", "Test County","Test PostCode"), 3,
                Arrays.asList(new URL("http://imageurl1"), new URL("http://imageurl2"),
                        new URL("http://imageurl3")), FIRST_AGENT, 2300);

        mockMvc.perform(post("/agent/" + FIRST_AGENT.getId() + "/properties/rentals")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(property)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(header().string("location", matchesPattern("http://localhost/properties/\\d")));
    }


}
