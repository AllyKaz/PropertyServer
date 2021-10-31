package com.example.PropertyServer;

import com.example.PropertyServer.Agent.Agent;
import com.example.PropertyServer.Property.Property;
import com.example.PropertyServer.Property.RentalProperty;
import com.example.PropertyServer.Property.SaleProperty;
import com.example.PropertyServer.Repositories.AgentRepository;
import com.example.PropertyServer.Repositories.PropertyBaseRepository;
import com.example.PropertyServer.Repositories.RentalPropertyRepository;
import com.example.PropertyServer.Repositories.SalePropertyRepository;
import com.example.PropertyServer.Services.AgentService;
import com.example.PropertyServer.Services.PropertyService;
import com.example.PropertyServer.TestUtils.RentalPropertyMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.PropertyServer.Builders.BuilderDirector.*;
import static com.example.PropertyServer.TestUtils.TestUtils.buildImageMultiPart;
import static com.example.PropertyServer.TestUtils.TestUtils.buildPropertyMultiPart;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
public class WebLayerTests {

    @MockBean
    private PropertyBaseRepository<Property> propertyRepository;
    @MockBean
    private RentalPropertyRepository rentalPropertyRepository;
    @MockBean
    private SalePropertyRepository salePropertyRepository;

    @MockBean
    private AgentRepository agentRepository;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private AgentService agentService;

    @Autowired
    MockMvc mockMvc;

    @Captor
    ArgumentCaptor<Map<String, String>> mapCaptor;

    @Captor
    ArgumentCaptor<Agent> agentCaptor;

    @Captor
    ArgumentCaptor<RentalProperty> rentalCaptor;

    @Captor
    ArgumentCaptor<SaleProperty> saleCaptor;

    @Captor
    ArgumentCaptor<MultipartFile[]> fileCaptor;


    private static final ObjectMapper mapper = new ObjectMapper();

    private final String KEY = "key";
    private final String VALUE = "value";

    private final String TIMESTAMP_REGEX = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d*";


    @Test
    public void getAllProperties() throws Exception {
        List<Property> propertyList = Arrays.asList(initRentalProperty(initAgent().build()).build(),
                initRentalProperty(initAgent().build()).build(), initSaleProperty(initAgent().build()).build());
        when(propertyService.getAllProperties(anyMap())).thenReturn(propertyList);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(KEY, VALUE);

        mockMvc.perform(get("/properties").queryParams(params))
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(propertyList)));

        verify(propertyService).getAllProperties(mapCaptor.capture());
        assertThat(mapCaptor.getValue(), hasEntry(KEY, VALUE));
    }

    @Test
    public void getRentalProperties() throws Exception {
        List<RentalProperty> propertyList = Arrays.asList(initRentalProperty(initAgent().build()).build(),
                initRentalProperty(initAgent().build()).build(), initRentalProperty(initAgent().build()).build());
        when(propertyService.getAllRentalProperties(anyMap())).thenReturn(propertyList);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(KEY, VALUE);

        mockMvc.perform(get("/rentalProperties").queryParams(params))
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(propertyList)));

        verify(propertyService).getAllRentalProperties(mapCaptor.capture());
        assertThat(mapCaptor.getValue(), hasEntry(KEY, VALUE));
    }

    @Test
    public void getSaleProperties() throws Exception {
        List<SaleProperty> propertyList = Arrays.asList(initSaleProperty(initAgent().build()).build(),
                initSaleProperty(initAgent().build()).build(), initSaleProperty(initAgent().build()).build());

        when(propertyService.getAllSaleProperties(anyMap())).thenReturn(propertyList);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(KEY,VALUE);

        mockMvc.perform(get("/saleProperties").queryParams(params))
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(propertyList)));

        verify(propertyService).getAllSaleProperties(mapCaptor.capture());
        assertThat(mapCaptor.getValue(), hasEntry(KEY,VALUE));
    }

    @Test
    public void getAllAgents() throws Exception {
        List<Agent> agentList = Arrays.asList(initAgent().build(), initAgent().build(), initAgent().build());

        when(agentService.getAll()).thenReturn(agentList);

        mockMvc.perform(get("/agents"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(agentList)));
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void addRentalProperty() throws Exception {
        Agent agent = initAgent().build();
        agent.setId(1);
        RentalProperty property = initRentalProperty(agent).build();
        List<MockMultipartFile> files = Arrays.asList(buildImageMultiPart(), buildImageMultiPart(),
                buildImageMultiPart());
        when(propertyService.createRentalProperty(any(), anyInt(), any())).thenReturn(property);

        mockMvc.perform(multipart("/agents/" + agent.getId() + "/properties/rentals")
                .file(files.get(0)).file(files.get(1)).file(files.get(0))
                .file(buildPropertyMultiPart(property)).with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(property)));

        verify(propertyService, times(1)).createRentalProperty(
                argThat(new RentalPropertyMatcher(property)),
                eq(agent.getId()), fileCaptor.capture());

        assertArrayEquals(fileCaptor.getValue()[0].getBytes(), files.get(0).getBytes());
        assertArrayEquals(fileCaptor.getValue()[1].getBytes(), files.get(1).getBytes());
        assertArrayEquals(fileCaptor.getValue()[2].getBytes(), files.get(2).getBytes());

    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void addSaleProperty() throws Exception {
        Agent agent = initAgent().build();
        agent.setId(1);
        SaleProperty property = initSaleProperty(agent).build();

        when(propertyService.createSaleProperty(any(SaleProperty.class), eq(agent.getId()), any(MultipartFile[].class)))
                .thenReturn(property);

        List<MockMultipartFile> files = Arrays.asList(buildImageMultiPart(), buildImageMultiPart(),
                buildImageMultiPart());

        mockMvc.perform(multipart("/agents/" + agent.getId() + "/properties/sales")
                .file(buildPropertyMultiPart(property))
                .file(files.get(0)).file(files.get(0)).file(files.get(0)).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(property)));

        verify(propertyService).createSaleProperty(saleCaptor.capture(), eq(agent.getId()), fileCaptor.capture());

        assertEquals(saleCaptor.getValue().getId(), property.getId());
        assertEquals(saleCaptor.getValue().getType(), property.getType());
        assertEquals(saleCaptor.getValue().getLocation(), property.getLocation());
        assertEquals(saleCaptor.getValue().getBedrooms(), property.getBedrooms());
        assertEquals(saleCaptor.getValue().getImages().size(), property.getImages().size());
        assertEquals(saleCaptor.getValue().getPrice(), property.getPrice());

        assertArrayEquals(fileCaptor.getValue()[0].getBytes(), files.get(0).getBytes());
        assertArrayEquals(fileCaptor.getValue()[1].getBytes(), files.get(1).getBytes());
        assertArrayEquals(fileCaptor.getValue()[2].getBytes(), files.get(2).getBytes());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void createAgent() throws Exception {
        Agent agent = initAgent().build();
        MockMultipartFile logoFile = new MockMultipartFile("logo", "logo",
                "image/jpeg", "image".getBytes());
        MockMultipartFile agentFile = new MockMultipartFile("agent", "agent",
                "application/json", mapper.writeValueAsString(agent).getBytes());

        when(agentService.createAgent(any(Agent.class), any(MultipartFile.class))).thenReturn(agent);

        mockMvc.perform(multipart("/agents").file(agentFile).file(logoFile).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(agent)));

        ArgumentCaptor<MultipartFile> fileCaptor = forClass(MultipartFile.class);
        verify(agentService).createAgent(agentCaptor.capture(), fileCaptor.capture());

        assertEquals(agentCaptor.getValue().getId(), agent.getId());
        assertEquals(agentCaptor.getValue().getName(), agent.getName());
        assertEquals(agentCaptor.getValue().getLocation(), agent.getLocation());
        assertEquals(agentCaptor.getValue().getTelephoneNumber(), agent.getTelephoneNumber());
        assertEquals(agentCaptor.getValue().getProperties(), agent.getProperties());
        assertEquals(agentCaptor.getValue().getLogoImage(), agent.getLogoImage());

        assertEquals(fileCaptor.getValue().getBytes(), logoFile.getBytes());

    }

    @Test
    public void getPropertyById() throws Exception {
        RentalProperty property = initRentalProperty(initAgent().build()).build();
        property.setId(1);
        when(propertyService.getProperty(property.getId())).thenReturn(property);

        mockMvc.perform(get("/properties/" + property.getId()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(property)));
    }

    @Test
    public void getNonexistentPropertyReturns404() throws Exception {
        final int PROPERTY_ID = 1;
        String message = "Property with id = " + PROPERTY_ID + " not found.";
        PropertyNotFoundException exception = new PropertyNotFoundException(PROPERTY_ID);
        when(propertyService.getProperty(PROPERTY_ID)).thenThrow(exception);

        mockMvc.perform(get("/properties/" + PROPERTY_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.length()", equalTo(1)))
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX)))
                .andExpect(jsonPath("$.errors[0]", equalTo(exception.getMessage())));
    }

    @Test
    public void getAgentById() throws Exception {
        Agent agent = initAgent().build();
        int AGENT_ID = 1;
        when(agentService.getAgent(AGENT_ID)).thenReturn(agent);

        mockMvc.perform(get("/agents/" + AGENT_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(agent)));
    }

    @Test
    public void getAgentProperties() throws Exception {
        Agent agent = initAgent().build();
        final int AGENT_ID = 1;
        RentalProperty rentalProperty = initRentalProperty(agent).build();
        SaleProperty saleProperty = initSaleProperty(agent).build();

        when(agentService.getAgentProperties(AGENT_ID)).thenReturn(Arrays.asList(rentalProperty,saleProperty));

        mockMvc.perform(get("/agents/" + AGENT_ID + "/properties"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(rentalProperty, saleProperty))));

    }

    @Test
    public void getAgentForProperties() throws Exception {
        Agent agent = initAgent().build();
        final int PROPERTY_ID = 1;
        Property property = initRentalProperty(agent).build();

        when(agentService.getAgentForProperty(PROPERTY_ID)).thenReturn(agent);

        mockMvc.perform(get("/properties/" + PROPERTY_ID + "/agent"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(agent)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void invalidRentalPropertyReturnsBadRequestAndErrorMessage() throws Exception {
        RentalProperty property = initRentalProperty(initAgent().build())
                .withType(null).withLocation(null).withBedrooms(0)
                .withImages(Collections.emptyList()).build();
        final int AGENT_ID = 1;

        mockMvc.perform(multipart("/agents/" + AGENT_ID + "/properties/rentals")
                .file(buildPropertyMultiPart(property)).file(buildImageMultiPart())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX)))
                .andExpect(jsonPath("$.errors.length()", equalTo(3)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void invalidSalePropertyReturnsBadRequestAndErrorMessage() throws Exception {
        SaleProperty property = initSaleProperty(initAgent().build())
                .withType(null).withLocation(null).withBedrooms(0)
                .withImages(Collections.emptyList()).build();
        final int AGENT_ID = 1;

        mockMvc.perform(multipart("/agents/" + AGENT_ID + "/properties/sales")
                .file(buildPropertyMultiPart(property)).file(buildImageMultiPart())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX)))
                .andExpect(jsonPath("$.errors.length()", equalTo(3)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void invalidAgentReturnsBadRequest() throws Exception {
        Agent agent = initAgent().withName(null).withLocation(null).build();
        MockMultipartFile agentFile = new MockMultipartFile("agent", "agent",
                "application/json", mapper.writeValueAsString(agent).getBytes());

        MockMultipartFile logoFile = new MockMultipartFile("logo", "logo",
                "image/jpeg", "image".getBytes());

        mockMvc.perform(multipart("/agents").file(agentFile).file(logoFile).with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX)))
                .andExpect(jsonPath("$.errors.length()", equalTo(2)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.toString())));
    }

    @Test
    @WithMockUser(username = "admin", roles="ADMIN")
    public void invalidLocationReturnsBadRequest() throws Exception {
        Agent agent = initAgent().build();
        RentalProperty rentalProperty = initRentalProperty(agent)
                .withLocation(initLocation().withNumber(0).withStreet("").withCity("").withPostcode("").build()).build();


        mockMvc.perform(multipart("/agents/" + 1 + "/properties/rentals")
                .file(buildPropertyMultiPart(rentalProperty)).file(buildImageMultiPart())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX)))
                .andExpect(jsonPath("$.errors.length()", equalTo(4)));

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void throwsIOException() throws Exception {
        SaleProperty saleProperty = initSaleProperty(initAgent().build()).build();

        when(propertyService.createSaleProperty(any(SaleProperty.class), anyInt(), any(MultipartFile[].class)))
                .thenThrow(new IOException());
        final int AGENT_ID = 1;

        mockMvc.perform(multipart("/agents/" + AGENT_ID + "/properties/sales")
                .file(buildPropertyMultiPart(saleProperty)).file(buildImageMultiPart())
                .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX)))
                .andExpect(jsonPath("$.errors.length()", equalTo(0)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.toString())));
    }

    @Test
    public void getAgentThrowsError404() throws Exception {
        int AGENT_ID = 1;
        when(agentService.getAgent(AGENT_ID)).thenThrow(new AgentNotFoundException(AGENT_ID));

        mockMvc.perform(get("/agents/" + AGENT_ID))
                .andDo(print())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX)))
                .andExpect(jsonPath("$.errors.length()", equalTo(1)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.NOT_FOUND.toString())));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void createRentalPropertyThrows404ForAgent() throws Exception {
        int AGENT_ID = 1;
        when(propertyService.createRentalProperty(any(RentalProperty.class), anyInt(), any(MultipartFile[].class)))
                .thenThrow(new AgentNotFoundException(AGENT_ID));
        MockMultipartFile property = buildPropertyMultiPart(initRentalProperty(initAgent().build()).build());

        mockMvc.perform(multipart("/agents/" + AGENT_ID + "/properties/rentals")
                .file(property).file(buildImageMultiPart()).with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX)))
                .andExpect(jsonPath("$.errors.length()", equalTo(1)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.NOT_FOUND.toString())));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void createSalePropertyThrows404ForAgent() throws Exception {
        int AGENT_ID = 1;
        when(propertyService.createSaleProperty(any(SaleProperty.class), anyInt(), any(MultipartFile[].class)))
                .thenThrow(new AgentNotFoundException(AGENT_ID));
        MockMultipartFile property = buildPropertyMultiPart(initSaleProperty(initAgent().build()).build());

        mockMvc.perform(multipart("/agents/" + AGENT_ID + "/properties/sales")
                .file(property).file(buildImageMultiPart()).with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX)))
                .andExpect(jsonPath("$.errors.length()", equalTo(1)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.NOT_FOUND.toString())));
    }

    @Test
    public void getAgentForPropertyThrows404ForProperty() throws Exception {
        int PROPERTY_ID = 1;
        when(agentService.getAgentForProperty(PROPERTY_ID)).thenThrow(new PropertyNotFoundException(PROPERTY_ID));

        mockMvc.perform(get("/properties/" + PROPERTY_ID + "/agent"))
                .andDo(print())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX)))
                .andExpect(jsonPath("$.errors.length()", equalTo(1)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.NOT_FOUND.toString())));
    }

    @Test
    public void getPropertiesBySpecification() throws Exception {
        MultiValueMap<String, String> params = buildParams();
        List<Property> properties = Arrays.asList(initRentalProperty(initAgent().build()).build(),
                initRentalProperty(initAgent().build()).build(), initRentalProperty(initAgent().build()).build());

        when(propertyService.getAllProperties(eq(params.toSingleValueMap()))).thenReturn(properties);

        mockMvc.perform(get("/properties").queryParams(params))
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(properties)));
    }

    @Test
    public void getRentalPropertiesBySpecification() throws Exception {
        MultiValueMap<String, String> params = buildParams();
        Agent agent = initAgent().build();
        List<RentalProperty> properties = Arrays.asList(initRentalProperty(agent).build(),
                initRentalProperty(agent).build(), initRentalProperty(agent).build());

        when(propertyService.getAllRentalProperties(eq(params.toSingleValueMap()))).thenReturn(properties);

        mockMvc.perform(get("/rentalProperties").queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(properties)));
    }

    @Test
    public void getSalePropertiesBySpecification() throws Exception {
        MultiValueMap<String, String> params = buildParams();
        Agent agent = initAgent().build();
        List<SaleProperty> properties = Arrays.asList(initSaleProperty(agent).build(),
                initSaleProperty(agent).build(), initSaleProperty(agent).build());

        when(propertyService.getAllSaleProperties(eq(params.toSingleValueMap()))).thenReturn(properties);

        mockMvc.perform(get("/saleProperties").queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(properties)));

    }
    /*
    static List<MultiValueMap<String, String>> getPropertiesBySpecification_unused() {
        List<MultiValueMap<String, String>> result = new ArrayList<MultiValueMap<String, String>>();
        MultiValueMap<String, String> tempMap = new LinkedMultiValueMap<>();
        generateParams(result, 0, tempMap, 5);
        return result;
    }

    private static String[] names = new String[]{"city", "postcode", "type", "min", "max"};

    private static void generateParams(List<MultiValueMap<String, String>> result, int i,
                                       MultiValueMap<String, String> tempMap, int n) {

        if(i == n) {
            result.add(new LinkedMultiValueMap<>(tempMap));
            return;
        }

        tempMap.put(names[i], Arrays.asList(names[i]));
        generateParams(result, i +1, tempMap, n);

        tempMap.remove(names[i]);
        generateParams(result, i + 1, tempMap, n);
    }*/
    private MultiValueMap<String, String> buildParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("key", "value");
        params.add("key_2", "value_2");
        return params;
    }














}
