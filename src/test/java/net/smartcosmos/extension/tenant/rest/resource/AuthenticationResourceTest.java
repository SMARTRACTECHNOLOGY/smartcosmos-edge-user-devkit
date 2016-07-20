package net.smartcosmos.extension.tenant.rest.resource;

import java.util.Arrays;
import java.util.Optional;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.GetAuthoritiesResponse;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationResourceTest extends AbstractTestResource {

    @Autowired
    protected TenantDao tenantDao;

    @After
    public void tearDown() throws Exception {
        reset(tenantDao);
    }

    @Test
    public void thatLoginSucceeds() throws Exception {

        RestAuthenticateRequest request = RestAuthenticateRequest.builder()
            .username("username")
            .password("password")
            .build();


        String[] authorities = {"smartcosmos.things.read", "smartcosmos.things.write"};

        GetAuthoritiesResponse response1 = GetAuthoritiesResponse.builder()
            .urn("urn")
            .tenantUrn("tenantUrn")
            .username("username")
            .authorities(Arrays.asList(authorities))
            .build();
        Optional<GetAuthoritiesResponse> response = Optional.of(response1);

        when(tenantDao.getAuthorities(anyString(), anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            post("/authenticate")
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.urn", is("urn")))
            .andExpect(jsonPath("$.username", is("username")))
            .andExpect(jsonPath("$.tenantUrn", is("tenantUrn")))
            .andExpect(jsonPath("$.authorities", hasSize(2)))
            .andExpect(jsonPath("$.authorities[0]", is("smartcosmos.things.read")))
            .andExpect(jsonPath("$.authorities[1]", is("smartcosmos.things.write")))
            .andExpect(jsonPath("$.authorities").isArray())
            .andReturn();

        verify(tenantDao, times(1)).getAuthorities(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatLoginUnauthorizedFails() throws Exception {

        RestAuthenticateRequest request = RestAuthenticateRequest.builder()
            .username("invalid")
            .password("invalid")
            .build();

        when(tenantDao.getAuthorities(anyString(), anyString())).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
            post("/authenticate")
                .content(json(request))
                .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(tenantDao, times(1)).getAuthorities(anyString(), anyString());
        verifyNoMoreInteractions(tenantDao);
    }

}
