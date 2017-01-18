package net.smartcosmos.usermanagement.role.resource;

import java.util.ArrayList;
import java.util.List;

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

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static net.smartcosmos.test.util.CommonTestConstants.CONTENT_TYPE_JSON;

/**
 * Unit Testing sample for deleting Roles.
 */
@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserManagementService.class, ResourceTestConfiguration.class })
@WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/roles/delete" })
public class DeleteRoleResourceTest {

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
     * Test that deleting a Role is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatDeleteRoleSucceeds() throws Exception {

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();
        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid()
            .toString();

        RoleResponse deleteResponse = RoleResponse.builder()
            .urn(expectedRoleUrn)
            .tenantUrn(expectedTenantUrn)
            .active(true)
            .build();
        List<RoleResponse> responseList = new ArrayList<>();
        responseList.add(deleteResponse);

        when(roleDao.delete(anyString(), anyString())).thenReturn(responseList);

        MvcResult mvcResult = this.mockMvc.perform(
            delete("/roles/{urn}", expectedRoleUrn).contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNoContent());
    }

    /**
     * Test that deleting a nonexistent Role fails.
     *
     * @throws Exception
     */
    @Test
    public void thatDeleteNonExistentRoleFails() throws Exception {

        final String expectedRoleUrn = "urn:role:uuid:" + UuidUtil.getNewUuid()
            .toString();

        when(roleDao.delete(anyString(), anyString())).thenReturn(new ArrayList<>());

        MvcResult mvcResult = this.mockMvc.perform(
            delete("/roles/{urn}", expectedRoleUrn).contentType(CONTENT_TYPE_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isNotFound());
    }
}
