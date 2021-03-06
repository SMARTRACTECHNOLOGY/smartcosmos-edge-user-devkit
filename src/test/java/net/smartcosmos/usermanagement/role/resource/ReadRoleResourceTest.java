package net.smartcosmos.usermanagement.role.resource;

import java.util.ArrayList;
import java.util.Arrays;
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
import net.smartcosmos.usermanagement.role.dto.RoleResponse;
import net.smartcosmos.usermanagement.role.persistence.RoleDao;

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
@WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/roles/read" })
public class ReadRoleResourceTest {

    @Autowired
    protected RoleDao roleDao;

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
        reset(roleDao);
    }

    // endregion

    @Test
    public void thatGetByUrnSucceeds() throws Exception {

        String name = "getByUrn";
        String urn = UuidUtil.getRoleUrnFromUuid(UuidUtil.getNewUuid());
        String tenantUrn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());
        Boolean active = true;

        String[] authorities = { "auth1", "auth2" };

        RoleResponse response1 = RoleResponse.builder()
            .active(active)
            .name(name)
            .urn(urn)
            .tenantUrn(tenantUrn)
            .authorities(Arrays.asList(authorities))
            .build();
        Optional<RoleResponse> response = Optional.of(response1);

        when(roleDao.findRoleByUrn(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/roles/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(active)))
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andExpect(jsonPath("$.tenantUrn", is(tenantUrn)))
            .andExpect(jsonPath("$.authorities").isArray())
            .andExpect(jsonPath("$.authorities[*]", contains("auth1", "auth2")))
            .andReturn();

        verify(roleDao, times(1)).findRoleByUrn(anyString(), anyString());
        verifyNoMoreInteractions(roleDao);
    }

    @Test
    public void thatGetByUrnFails() throws Exception {

        String urn = UuidUtil.getRoleUrnFromUuid(UuidUtil.getNewUuid());

        Optional<RoleResponse> response = Optional.empty();

        when(roleDao.findRoleByUrn(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/roles/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(roleDao, times(1)).findRoleByUrn(anyString(), anyString());
        verifyNoMoreInteractions(roleDao);
    }

    @Test
    public void thatGetByNameSucceeds() throws Exception {

        String name = "getByName";
        String urn = UuidUtil.getRoleUrnFromUuid(UuidUtil.getNewUuid());
        String tenantUrn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        RoleResponse response1 = RoleResponse.builder()
            .active(true)
            .name(name)
            .urn(urn)
            .tenantUrn(tenantUrn)
            .build();
        Optional<RoleResponse> response = Optional.of(response1);

        when(roleDao.findRoleByName(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/roles")
                .param("name", name)
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andExpect(jsonPath("$.tenantUrn", is(tenantUrn)))
            .andReturn();

        verify(roleDao, times(1)).findRoleByName(anyString(), anyString());
        verifyNoMoreInteractions(roleDao);
    }

    @Test
    public void thatGetByNameFails() throws Exception {

        String name = "noSuchRole";

        Optional<RoleResponse> response = Optional.empty();

        when(roleDao.findRoleByName(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/roles")
                .param("name", name)
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(roleDao, times(1)).findRoleByName(anyString(), anyString());
        verifyNoMoreInteractions(roleDao);
    }

    @Test
    public void thatFindAllRolesInTenantSucceeds() throws Exception {

        List<RoleResponse> response = new ArrayList<>();
        response.add(RoleResponse.builder()
                         .active(true)
                         .name("name1")
                         .urn("urn1")
                         .build());
        response.add(RoleResponse.builder()
                         .active(true)
                         .name("name2")
                         .urn("urn2")
                         .build());

        when(roleDao.findAllRoles(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/roles")
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].urn", contains("urn1", "urn2")))
            .andExpect(jsonPath("$[*].name", contains("name1", "name2")))
            .andReturn();

        verify(roleDao, times(1)).findAllRoles(anyString());
        verifyNoMoreInteractions(roleDao);
    }
}
