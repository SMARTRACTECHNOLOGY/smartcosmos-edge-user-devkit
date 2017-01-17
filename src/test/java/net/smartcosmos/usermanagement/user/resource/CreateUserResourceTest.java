package net.smartcosmos.usermanagement.user.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.test.AbstractTestResource;
import net.smartcosmos.test.security.WithMockSmartCosmosUser;
import net.smartcosmos.usermanagement.DevKitUserManagementService;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;
import net.smartcosmos.usermanagement.user.dto.CreateUserRequest;
import net.smartcosmos.usermanagement.user.dto.CreateUserResponse;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit Testing sample for creating Users.
 */
@org.springframework.boot.test.SpringApplicationConfiguration(classes = { DevKitUserManagementService.class })
@WithMockSmartCosmosUser(authorities = { "https://authorities.smartcosmos.net/users/create" })
public class CreateUserResourceTest extends AbstractTestResource {

    @Autowired
    protected TenantDao tenantDao;

    @After
    public void tearDown() throws Exception {

        reset(tenantDao);
    }

    /**
     * Test that creating a User is successful.
     *
     * @throws Exception
     */
    @Test
    public void thatCreateUserSucceeds() throws Exception {

        String username = "newUser";
        String emailAddress = "newUser@example.com";

        final String expectedTenantUrn = "urn:tenant:uuid:" + UuidUtil.getNewUuid()
            .toString();

        final String expectedUserUrn = "urn:user:uuid:" + UuidUtil.getNewUuid()
            .toString();

        Set<String> userRoles = new HashSet<>();
        userRoles.add("User");

        CreateUserResponse createUserResponse = CreateUserResponse.builder()
            .urn(expectedUserUrn)
            .tenantUrn(expectedTenantUrn)
            .username(username)
            .roles(userRoles)
            .build();

        when(tenantDao.createUser(anyString(), anyObject())).thenReturn(Optional.ofNullable(createUserResponse));

        CreateUserRequest request = CreateUserRequest.builder()
            .username(username)
            .emailAddress(emailAddress)
            .roles(userRoles)
            .build();

        org.springframework.test.web.servlet.MvcResult mvcResult = this.mockMvc.perform(
            post("/users")
                .content(this.json(request))
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andReturn();

        MvcResult result = this.mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.urn", startsWith("urn:user:uuid")))
            .andExpect(jsonPath("$.username", is(username)))
            .andExpect(jsonPath("$.emailAddress").doesNotExist())
            .andExpect(jsonPath("$.tenantUrn", startsWith("urn:tenant:uuid")))
            .andReturn();
    }

}
