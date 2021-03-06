package net.smartcosmos.usermanagement.user.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.test.config.ResourceTestConfiguration;
import net.smartcosmos.test.security.WithMockSmartCosmosUser;
import net.smartcosmos.usermanagement.DevKitUserManagementService;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;
import net.smartcosmos.usermanagement.user.dto.UserResponse;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserManagementService.class, ResourceTestConfiguration.class })
@WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/users/read" })
public class ReadUserResourceTest {

    @Autowired
    protected TenantDao tenantDao;

    @Autowired
    WebApplicationContext webApplicationContext;
    MockMvc mockMvc;

    // region Setup

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    @After
    public void tearDown() throws Exception {

        validateMockitoUsage();
        reset(tenantDao);
    }

    // endregion

    @Test
    public void thatGetByUrnSucceeds() throws Exception {

        String name = "getByUrn";
        String urn = UuidUtil.getUserUrnFromUuid(UuidUtil.getNewUuid());
        String tenantUrn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());
        String emailAddress = "getByUrn@example.com";
        Boolean active = true;
        String givenName = "John";
        String surname = "Doe";

        UserResponse response1 = UserResponse.builder()
            .active(active)
            .username(name)
            .emailAddress(emailAddress)
            .urn(urn)
            .tenantUrn(tenantUrn)
            .givenName(givenName)
            .surname(surname)
            .build();
        Optional<UserResponse> response = Optional.of(response1);

        when(tenantDao.findUserByUrn(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/users/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(active)))
            .andExpect(jsonPath("$.givenName", is(givenName)))
            .andExpect(jsonPath("$.surname", is(surname)))
            .andExpect(jsonPath("$.emailAddress", is(emailAddress)))
            .andExpect(jsonPath("$.username", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andExpect(jsonPath("$.tenantUrn", is(tenantUrn)))
            .andExpect(jsonPath("$.roles").isArray())
            .andReturn();

        verify(tenantDao, times(1)).findUserByUrn(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser(authorities = {}, usernUrn = "myOwnUrn")
    public void thatGetByOwnUrnSucceeds() throws Exception {

        String name = "getByUrn";
        String urn = "myOwnUrn";
        String tenantUrn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());
        String emailAddress = "getByUrn@example.com";
        Boolean active = true;
        String givenName = "John";
        String surname = "Doe";

        UserResponse response1 = UserResponse.builder()
            .active(active)
            .username(name)
            .emailAddress(emailAddress)
            .urn(urn)
            .tenantUrn(tenantUrn)
            .givenName(givenName)
            .surname(surname)
            .build();
        Optional<UserResponse> response = Optional.of(response1);

        when(tenantDao.findUserByUrn(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/users/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(active)))
            .andExpect(jsonPath("$.givenName", is(givenName)))
            .andExpect(jsonPath("$.surname", is(surname)))
            .andExpect(jsonPath("$.emailAddress", is(emailAddress)))
            .andExpect(jsonPath("$.username", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andExpect(jsonPath("$.tenantUrn", is(tenantUrn)))
            .andExpect(jsonPath("$.roles").isArray())
            .andReturn();

        verify(tenantDao, times(1)).findUserByUrn(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByUrnFails() throws Exception {

        String urn = UuidUtil.getUserUrnFromUuid(UuidUtil.getNewUuid());

        Optional<UserResponse> response = Optional.empty();

        when(tenantDao.findUserByUrn(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/users/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(tenantDao, times(1)).findUserByUrn(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser(authorities = {}, username = "myOwnName")
    public void thatGetByOwnNameSucceeds() throws Exception {

        String name = "myOwnName";
        String urn = UuidUtil.getUserUrnFromUuid(UuidUtil.getNewUuid());
        String tenantUrn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        UserResponse response1 = UserResponse.builder()
            .active(true)
            .username(name)
            .urn(urn)
            .tenantUrn(tenantUrn)
            .build();
        Optional<UserResponse> response = Optional.of(response1);

        when(tenantDao.findUserByName(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/users")
                .param("name", name)
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.username", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andExpect(jsonPath("$.tenantUrn", is(tenantUrn)))
            .andReturn();

        verify(tenantDao, times(1)).findUserByName(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByNameSucceeds() throws Exception {

        String name = "getByName";
        String urn = UuidUtil.getUserUrnFromUuid(UuidUtil.getNewUuid());
        String tenantUrn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        UserResponse response1 = UserResponse.builder()
            .active(true)
            .username(name)
            .urn(urn)
            .tenantUrn(tenantUrn)
            .build();
        Optional<UserResponse> response = Optional.of(response1);

        when(tenantDao.findUserByName(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/users")
                .param("name", name)
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.username", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andExpect(jsonPath("$.tenantUrn", is(tenantUrn)))
            .andReturn();

        verify(tenantDao, times(1)).findUserByName(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByNameFails() throws Exception {

        String name = "noSuchUser";

        Optional<UserResponse> response = Optional.empty();

        when(tenantDao.findUserByName(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/users")
                .param("name", name)
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(tenantDao, times(1)).findUserByName(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatFindAllUsersInTenantSucceeds() throws Exception {

        List<UserResponse> response = new ArrayList<>();
        response.add(UserResponse.builder()
                         .active(true)
                         .username("name1")
                         .urn("urn1")
                         .build());
        response.add(UserResponse.builder()
                         .active(true)
                         .username("name2")
                         .urn("urn2")
                         .build());

        when(tenantDao.findAllUsers(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/users")
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].urn", contains("urn1", "urn2")))
            .andExpect(jsonPath("$[*].username", contains("name1", "name2")))
            .andReturn();

        verify(tenantDao, times(1)).findAllUsers(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser
    public void thatFindAllUsersInTenantWithoutAuthorityFails() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
            get("/users")
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden())
            .andReturn();

        verifyNoMoreInteractions(tenantDao);
    }
}
