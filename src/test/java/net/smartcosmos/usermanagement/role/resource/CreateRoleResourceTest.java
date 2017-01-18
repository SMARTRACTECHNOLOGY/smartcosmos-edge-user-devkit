package net.smartcosmos.usermanagement.role.resource;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
import net.smartcosmos.usermanagement.role.dto.RoleRequest;
import net.smartcosmos.usermanagement.role.dto.RoleResponse;
import net.smartcosmos.usermanagement.role.persistence.RoleDao;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static net.smartcosmos.test.util.CommonTestConstants.CONTENT_TYPE_JSON;
import static net.smartcosmos.test.util.TestUtil.json;

/**
 * Unit Testing sample for creating Roles.
 */
@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserManagementService.class, ResourceTestConfiguration.class })
@WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/roles/create" })
public class CreateRoleResourceTest {

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

    /**
     * Test that creating a Role is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatCreateRoleSucceeds() throws Exception {

        String roleName = "newRole";
        Boolean active = true;

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid()
            .toString();

        Set<String> authorities = new HashSet<>();
        authorities.add("Authority1");
        authorities.add("Authority2");

        RoleResponse createRoleResponse = RoleResponse.builder()
            .urn(expectedRoleUrn)
            .name(roleName)
            .authorities(authorities)
            .active(active)
            .tenantUrn(expectedTenantUrn)
            .build();

        when(roleDao.createRole(anyString(), anyObject())).thenReturn(Optional.ofNullable(createRoleResponse));

        RoleRequest request = RoleRequest.builder()
            .name(roleName)
            .authorities(authorities)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/roles")
                .content(json(request))
                .contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(CONTENT_TYPE_JSON))
            .andExpect(jsonPath("$.urn", is(expectedRoleUrn)))
            .andExpect(jsonPath("$.name", is(roleName)))
            .andExpect(jsonPath("$.authorities").isArray())
            .andExpect(jsonPath("$.authorities", hasSize(2)))
            .andExpect(jsonPath("$.active", is(active)))
            .andExpect(jsonPath("$.tenantUrn", is(expectedTenantUrn)))
            .andReturn();
    }

}
