package net.smartcosmos.usermanagement.tenant.resource;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
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
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantRequest;
import net.smartcosmos.usermanagement.tenant.dto.CreateTenantResponse;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;
import net.smartcosmos.usermanagement.user.dto.CreateUserResponse;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Matchers.anyObject;
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
 * Unit Testing sample for creating Tenants.
 */
@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserManagementService.class, ResourceTestConfiguration.class })
public class CreateTenantResourceTest {

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

    /**
     * Test that creating a Tenant is successful.
     *
     * @throws Exception
     */
    @Test
    @WithMockSmartCosmosUser
    public void thatCreateTenantWithUserSucceeds() throws Exception {

        final String name = "example.com";
        final String username = "spam@example.com";
        final String password = RandomStringUtils.randomAlphanumeric(8);

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedUserUrn = "urn:user:uuid:" + UuidUtil.getNewUuid()
            .toString();

        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("Admin");

        CreateUserResponse createUserResponse = CreateUserResponse.builder()
            .urn(expectedUserUrn)
            .username(username)
            .password(password)
            .roles(adminRoles)
            .tenantUrn(expectedTenantUrn)
            .build();

        CreateTenantResponse createTenantResponse = CreateTenantResponse.builder()
            .urn(expectedTenantUrn)
            .admin(createUserResponse)
            .build();

        when(tenantDao.createTenant(anyObject())).thenReturn(Optional.ofNullable(createTenantResponse));

        CreateTenantRequest request = CreateTenantRequest.builder()
            .name(name)
            .username(username)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/tenants").content(json(request))
                .contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(CONTENT_TYPE_JSON))
            .andExpect(jsonPath("$.urn", startsWith("urn:tenant:uuid")))
            .andExpect(jsonPath("$.name").doesNotExist())
            .andExpect(jsonPath("$.admin").isMap())
            .andExpect(jsonPath("$.admin.emailAddress").doesNotExist())
            .andExpect(jsonPath("$.admin.username", is(username)))
            .andExpect(jsonPath("$.admin.password", is(password)))
            .andExpect(jsonPath("$.admin.roles").isArray())
            .andExpect(jsonPath("$.admin.tenantUrn", startsWith("urn:tenant:uuid")))
            .andExpect(jsonPath("$.admin.urn", startsWith("urn:user:uuid")));

    }

    @Test
    public void thatCreateTenantUnAuthenticatedSucceeds() throws Exception {

        final String name = "example.com";
        final String username = "spam@example.com";
        final String password = RandomStringUtils.randomAlphanumeric(8);

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedUserUrn = "urn:user:uuid:" + UuidUtil.getNewUuid()
            .toString();

        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("Admin");

        CreateUserResponse createUserResponse = CreateUserResponse.builder()
            .urn(expectedUserUrn)
            .username(username)
            .password(password)
            .roles(adminRoles)
            .tenantUrn(expectedTenantUrn)
            .build();

        CreateTenantResponse createTenantResponse = CreateTenantResponse.builder()
            .urn(expectedTenantUrn)
            .admin(createUserResponse)
            .build();

        when(tenantDao.createTenant(anyObject())).thenReturn(Optional.ofNullable(createTenantResponse));

        CreateTenantRequest request = CreateTenantRequest.builder()
            .name(name)
            .username(username)
            .build();

        MvcResult mvcResult = this.mockMvc.perform(
            post("/tenants").content(json(request))
                .contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(CONTENT_TYPE_JSON))
            .andExpect(jsonPath("$.urn", startsWith("urn:tenant:uuid")))
            .andExpect(jsonPath("$.name").doesNotExist())
            .andExpect(jsonPath("$.admin").isMap())
            .andExpect(jsonPath("$.admin.emailAddress").doesNotExist())
            .andExpect(jsonPath("$.admin.username", is(username)))
            .andExpect(jsonPath("$.admin.password", is(password)))
            .andExpect(jsonPath("$.admin.roles").isArray())
            .andExpect(jsonPath("$.admin.tenantUrn", startsWith("urn:tenant:uuid")))
            .andExpect(jsonPath("$.admin.urn", startsWith("urn:user:uuid")));

    }
}
