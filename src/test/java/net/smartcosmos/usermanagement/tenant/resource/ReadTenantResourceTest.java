package net.smartcosmos.usermanagement.tenant.resource;

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
import net.smartcosmos.usermanagement.tenant.dto.TenantResponse;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;

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
public class ReadTenantResourceTest {

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
    @WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/tenants/read" })
    public void thatGetByUrnSucceeds() throws Exception {

        String name = "getByUrn";
        String urn = "accountUrn"; // Tenant URN from AbstractTestResource

        TenantResponse response1 = TenantResponse.builder()
            .active(true)
            .name(name)
            .urn(urn)
            .build();
        Optional<TenantResponse> response = Optional.of(response1);

        when(tenantDao.findTenantByUrn(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andReturn();

        verify(tenantDao, times(1)).findTenantByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser(tenantUrn = "myOwnTenantUrn", authorities = {})
    public void thatGetByOwnUrnSucceeds() throws Exception {

        String name = "getByUrn";
        String urn = "myOwnTenantUrn";

        TenantResponse response1 = TenantResponse.builder()
            .active(true)
            .name(name)
            .urn(urn)
            .build();
        Optional<TenantResponse> response = Optional.of(response1);

        when(tenantDao.findTenantByUrn(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andReturn();

        verify(tenantDao, times(1)).findTenantByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByUrnAnonymousFails() throws Exception {

        String urn = "accountUrn"; // Tenant URN from AbstractTestResource

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(tenantDao, times(0)).findTenantByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/tenants/read" })
    public void thatGetByUnknownUrnFails() throws Exception {

        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        Optional<TenantResponse> response = Optional.empty();

        when(tenantDao.findTenantByUrn(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(tenantDao, times(1)).findTenantByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByUnknownUrnAnonymousFails() throws Exception {

        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(tenantDao, times(0)).findTenantByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/tenants/read" })
    public void thatGetByNameSucceeds() throws Exception {

        String name = "getByName";
        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        TenantResponse response1 = TenantResponse.builder()
            .active(true)
            .name(name)
            .urn(urn)
            .build();
        Optional<TenantResponse> response = Optional.of(response1);

        when(tenantDao.findTenantByName(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants")
                .param("name", name)
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andReturn();

        verify(tenantDao, times(1)).findTenantByName(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByNameAnonymousFails() throws Exception {

        String name = "getByName";

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants")
                .param("name", name)
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(tenantDao, times(0)).findTenantByName(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/tenants/read" })
    public void thatGetByUnknownNameFails() throws Exception {

        String name = "noSuchTenant";

        Optional<TenantResponse> response = Optional.empty();

        when(tenantDao.findTenantByName(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants")
                .param("name", name)
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(tenantDao, times(1)).findTenantByName(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByUnknownNameAnonymousFails() throws Exception {

        String name = "noSuchTenant";

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants")
                .param("name", name)
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(tenantDao, times(0)).findTenantByName(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/tenants/read" })
    public void thatGetAllNoTenantSucceeds() throws Exception {

        List<TenantResponse> response = new ArrayList<>();

        when(tenantDao.findAllTenants()).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants")
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty())
            .andReturn();

        verify(tenantDao, times(1)).findAllTenants();
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetAllNoTenantAnonymousFails() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants")
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(tenantDao, times(0)).findAllTenants();
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    @WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/tenants/read" })
    public void thatGetAllTenantSucceeds() throws Exception {

        List<TenantResponse> response = new ArrayList<>();
        response.add(TenantResponse.builder()
                         .active(true)
                         .name("name1")
                         .urn("urn1")
                         .build());
        response.add(TenantResponse.builder()
                         .active(true)
                         .name("name2")
                         .urn("urn2")
                         .build());

        when(tenantDao.findAllTenants()).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants")
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].urn", contains("urn1", "urn2")))
            .andExpect(jsonPath("$[*].name", contains("name1", "name2")))
            .andReturn();

        verify(tenantDao, times(1)).findAllTenants();
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetAllTenantAnonymousFails() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants")
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(tenantDao, times(0)).findAllTenants();
        verifyNoMoreInteractions(tenantDao);
    }
}
