package net.smartcosmos.extension.tenant.rest.resource;

import java.util.Optional;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.extension.tenant.dto.tenant.GetTenantResponse;
import net.smartcosmos.extension.tenant.util.UuidUtil;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReadTenantResourceTest extends AbstractTestResource {

    @Autowired
    protected TenantDao tenantDao;

    @After
    public void tearDown() throws Exception {
        reset(tenantDao);
    }

    @Test
    public void thatGetByUrnSucceeds() throws Exception {

        String name = "getByUrn";
        String urn = "accountUrn"; // Tenant URN from AbstractTestResource

        GetTenantResponse response1 = GetTenantResponse.builder()
            .active(true)
            .name(name)
            .urn(urn)
            .build();
        Optional<GetTenantResponse> response = Optional.of(response1);

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
    public void thatGetByForeignUrnFails() throws Exception {

        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        Optional<GetTenantResponse> response = Optional.empty();

        when(tenantDao.findTenantByUrn(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants/{urn}", urn).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(tenantDao, times(1)).findTenantByUrn(anyString());
        verifyNoMoreInteractions(tenantDao);
    }

    @Test
    public void thatGetByNameSucceeds() throws Exception {

        String name = "getByName";
        String urn = UuidUtil.getTenantUrnFromUuid(UuidUtil.getNewUuid());

        GetTenantResponse response1 = GetTenantResponse.builder()
            .active(true)
            .name(name)
            .urn(urn)
            .build();
        Optional<GetTenantResponse> response = Optional.of(response1);

        when(tenantDao.findTenantByName(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants/?name={name}", name).contentType(APPLICATION_JSON_UTF8))
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
    public void thatGetByNameFails() throws Exception {

        String name = "noSuchTenant";

        Optional<GetTenantResponse> response = Optional.empty();

        when(tenantDao.findTenantByName(anyString())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(
            get("/tenants/?name={name}", name).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound())
            .andReturn();

        verify(tenantDao, times(1)).findTenantByName(anyString());
        verifyNoMoreInteractions(tenantDao);
    }
}
