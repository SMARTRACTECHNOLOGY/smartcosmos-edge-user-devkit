package net.smartcosmos.extension.tenant.rest.resource;

import java.util.Optional;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.GetOrDeleteUserResponse;
import net.smartcosmos.extension.tenant.util.UuidUtil;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReadUserResourceTest extends AbstractTestResource {

    @Autowired
    protected TenantDao tenantDao;

    @After
    public void tearDown() throws Exception {
        reset(tenantDao);
    }

    @Test
    public void thatGetByUrnSucceeds() throws Exception {

        String name = "getByUrn";
        String urn = UuidUtil.getUserUrnFromUuid(UuidUtil.getNewUuid());
        String tenantUrn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        GetOrDeleteUserResponse response1 = GetOrDeleteUserResponse.builder()
            .active(true)
            .username(name)
            .emailAddress("getByUrn@example.com")
            .urn(urn)
            .tenantUrn(tenantUrn)
            .build();
        Optional<GetOrDeleteUserResponse> response = Optional.of(response1);

        when(tenantDao.findUserByUrn(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/users/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.username", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andExpect(jsonPath("$.tenantUrn", is(tenantUrn)))
            .andReturn();

        verify(tenantDao, times(1)).findUserByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByUrnFails() throws Exception {

        String urn = UuidUtil.getUserUrnFromUuid(UuidUtil.getNewUuid());

        Optional<GetOrDeleteUserResponse> response = Optional.empty();

        when(tenantDao.findUserByUrn(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/users/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(tenantDao, times(1)).findUserByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByNameSucceeds() throws Exception {

        String name = "getByName";
        String urn = UuidUtil.getUserUrnFromUuid(UuidUtil.getNewUuid());
        String tenantUrn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        GetOrDeleteUserResponse response1 = GetOrDeleteUserResponse.builder()
            .active(true)
            .username(name)
            .urn(urn)
            .tenantUrn(tenantUrn)
            .build();
        Optional<GetOrDeleteUserResponse> response = Optional.of(response1);

        when(tenantDao.findUserByName(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/users/?name={name}", name).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.username", is(name)))
            .andExpect(jsonPath("$.urn", is(urn)))
            .andExpect(jsonPath("$.tenantUrn", is(tenantUrn)))
            .andReturn();

        verify(tenantDao, times(1)).findUserByName(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByNameFails() throws Exception {

        String name = "noSuchUser";

        Optional<GetOrDeleteUserResponse> response = Optional.empty();

        when(tenantDao.findUserByName(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/users/?name={name}", name).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(tenantDao, times(1)).findUserByName(anyString());
        verifyNoMoreInteractions(tenantDao);
    }
}