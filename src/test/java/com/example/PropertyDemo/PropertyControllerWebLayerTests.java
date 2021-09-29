package com.example.PropertyDemo;

import com.example.PropertyDemo.Agent.Agent;
import com.example.PropertyDemo.Property.Property;
import com.example.PropertyDemo.Property.RentalProperty;
import com.example.PropertyDemo.Property.SaleProperty;
import com.example.PropertyDemo.Repositories.AgentRepository;
import com.example.PropertyDemo.Repositories.PropertyBaseRepository;
import com.example.PropertyDemo.Repositories.RentalPropertyRepository;
import com.example.PropertyDemo.Repositories.SalePropertyRepository;
import com.example.PropertyDemo.Services.AgentService;
import com.example.PropertyDemo.Services.PropertyService;
import com.example.PropertyDemo.TestUtils.RentalPropertyMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.PropertyDemo.Builders.BuilderDirector.*;
import static com.example.PropertyDemo.TestUtils.PropertyDemoTestHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
public class PropertyControllerWebLayerTests {

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

    private static final ObjectMapper mapper = new ObjectMapper();


    @Test
    public void getAllProperties() throws Exception {
        performGetAllProperties("/properties", Arrays.asList(generateRentalProperty(generateAgent()),
                generateSaleProperty(generateAgent())));
    }

    @Test
    public void getRentalProperties() throws Exception {
        performGetAllProperties("/rentalProperties", Arrays.asList(generateRentalProperty(generateAgent()),
                generateRentalProperty(generateAgent()), generateRentalProperty(generateAgent())));
    }

    @Test
    public void getSaleProperties() throws Exception {
        performGetAllProperties("/saleProperties", Arrays.asList(generateSaleProperty(generateAgent()),
                generateSaleProperty(generateAgent()), generateSaleProperty(generateAgent())));

    }

    private void performGetAllProperties(String url, List<Property> propertyList) throws Exception {
        final String KEY = "key", VALUE = "value";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(KEY, VALUE);

        when(propertyService.getAllProperties(anyMap())).thenReturn(propertyList);

        mockMvc.perform(get(url).queryParams(params))
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(propertyList)));

        ArgumentCaptor<Map<String, String>> captor = forClass(Map.class);
        verify(propertyService).getAllProperties(captor.capture());
        assertThat(captor.getValue(), hasEntry(KEY, VALUE));
    }

    @Test
    public void getAllAgents() throws Exception {
        List<Agent> agentList = Arrays.asList(generateAgent(), generateAgent(), generateAgent());

        when(agentService.getAll()).thenReturn(agentList);

        mockMvc.perform(get("/agents"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(agentList)));
    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void addRentalProperty() throws Exception {
        Agent agent = generateAgent();
        RentalProperty property = generateRentalProperty(agent);

        MockMultipartFile propertyMultipart = new MockMultipartFile("property", "property",
                "application/json", mapper.writeValueAsString(property).getBytes());

        MockMultipartFile multipartFile1 = new MockMultipartFile("images", "content".getBytes());
        MockMultipartFile multipartFile2 = new MockMultipartFile("images", "content".getBytes());
        MockMultipartFile multipartFile3 = new MockMultipartFile("images", "content".getBytes());

        when(propertyService.createRentalProperty(any(), anyInt(), any())).thenReturn(property);

        mockMvc.perform(multipart("/agents/" + agent.getId() + "/properties/rentals")
                .file(multipartFile1).file(multipartFile2).file(multipartFile3)
                .file(propertyMultipart).with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(property)));

        //ArgumentCaptor<RentalProperty> captor = forClass(RentalProperty.class);
        ArgumentCaptor<MultipartFile[]> fileCaptor = forClass(MultipartFile[].class);

        verify(propertyService, times(1)).createRentalProperty(
                argThat(new RentalPropertyMatcher(property)),
                eq(agent.getId()), fileCaptor.capture());

        /*assertEquals(captor.getValue().getId(), property.getId());
        assertEquals(captor.getValue().getType(), property.getType());
        assertEquals(captor.getValue().getLocation(), property.getLocation());
        assertEquals(captor.getValue().getBedrooms(), property.getBedrooms());
        assertEquals(captor.getValue().getImages().size(), property.getImages().size());
        assertEquals(captor.getValue().getMonthlyRent(), property.getMonthlyRent());*/

       assertEquals(fileCaptor.getValue()[0].getBytes(), multipartFile1.getBytes());
       assertEquals(fileCaptor.getValue()[1].getBytes(), multipartFile2.getBytes());
       assertEquals(fileCaptor.getValue()[2].getBytes(), multipartFile3.getBytes());

    }

    @Test
    @WithMockUser(username="admin", roles="ADMIN")
    public void addSaleProperty() throws Exception {
        Agent agent = generateAgent();
        SaleProperty property = generateSaleProperty(agent);

        MockMultipartFile propertyMultipartFile = new MockMultipartFile("property", "property",
                "application/json", mapper.writeValueAsString(property).getBytes());

        when(propertyService.createSaleProperty(any(SaleProperty.class), eq(agent.getId()), any(MultipartFile[].class)))
                .thenReturn(property);

        MockMultipartFile multipartFile1 = new MockMultipartFile("images", "content".getBytes());
        MockMultipartFile multipartFile2 = new MockMultipartFile("images", "content".getBytes());
        MockMultipartFile multipartFile3 = new MockMultipartFile("images", "content".getBytes());

        mockMvc.perform(multipart("/agents/" + agent.getId() + "/properties/sales").file(propertyMultipartFile)
                .file(multipartFile1).file(multipartFile2).file(multipartFile3).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(property)));

        ArgumentCaptor<SaleProperty> propertyCaptor = forClass(SaleProperty.class);
        ArgumentCaptor<MultipartFile[]> fileCaptor = forClass(MultipartFile[].class);

        verify(propertyService).createSaleProperty(propertyCaptor.capture(), eq(agent.getId()), fileCaptor.capture());

        assertEquals(propertyCaptor.getValue().getId(), property.getId());
        assertEquals(propertyCaptor.getValue().getType(), property.getType());
        assertEquals(propertyCaptor.getValue().getLocation(), property.getLocation());
        assertEquals(propertyCaptor.getValue().getBedrooms(), property.getBedrooms());
        assertEquals(propertyCaptor.getValue().getImages().size(), property.getImages().size());
        assertEquals(propertyCaptor.getValue().getPrice(), property.getPrice());

        assertEquals(fileCaptor.getValue()[0].getBytes(), multipartFile1.getBytes());
        assertEquals(fileCaptor.getValue()[1].getBytes(), multipartFile2.getBytes());
        assertEquals(fileCaptor.getValue()[2].getBytes(), multipartFile3.getBytes());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void createAgent() throws Exception {
        Agent agent = generateAgent();

        MockMultipartFile logoFile = new MockMultipartFile("logo", "logo", "image/jpeg",
                "imagedata".getBytes());

        MockMultipartFile agentFile = new MockMultipartFile("agent", "agent",
                "application/json", mapper.writeValueAsString(agent).getBytes());

        when(agentService.createAgent(any(Agent.class), any(MultipartFile.class))).thenReturn(agent);

        mockMvc.perform(multipart("/agents").file(agentFile).file(logoFile).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(agent)));

        ArgumentCaptor<Agent> agentCaptor = forClass(Agent.class);
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

        when(propertyService.getProperty(property.getId())).thenReturn(property);

        mockMvc.perform(get("/properties/" + property.getId()))
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(property)));
    }

    @Test
    public void getAgentById() throws Exception {
        Agent agent = initAgent().build();

        when(agentRepository.findById(agent.getId())).thenReturn(Optional.of(agent));

        mockMvc.perform(get("/agents/" + agent.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(agent)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void InvalidPropertyReturnsBadRequest() throws Exception {
        RentalProperty property = initRentalProperty(initAgent().build()).withType(null)
                .withImages(Collections.emptyList()).build();
        MockMultipartFile propertyFile = new MockMultipartFile("property", "property",
                "application/json", mapper.writeValueAsString(property).getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("images", "image".getBytes());
        final int AGENT_ID = 1;

        mockMvc.perform(multipart("/agents/" + AGENT_ID + "/properties/rentals")
                .file(propertyFile).file(imageFile)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());
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

        mockMvc.perform(get("/properties/rentals").queryParams(params))
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

        mockMvc.perform(get("/properties/sales").queryParams(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(properties)));

    }

    private MultiValueMap<String, String> buildParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("key", "value");
        params.add("key_2", "value_2");
        return params;
    }

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
    }












}
