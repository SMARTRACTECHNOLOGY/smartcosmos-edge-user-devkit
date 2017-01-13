package net.smartcosmos.extension.tenant.converter.tenant;

import java.util.UUID;

import org.junit.*;

import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.usermanagement.tenant.domain.TenantEntity;
import net.smartcosmos.usermanagement.tenant.dto.TenantResponse;
import net.smartcosmos.usermanagement.tenant.converter.TenantEntityToTenantResponseConverter;

import static org.junit.Assert.*;

public class TenantEntityToTenantResponseConverterTest {

    @Test
    public void thatConversionSucceeds() {

        final boolean active = true;
        final String name = "tenant_name";
        final UUID id = UuidUtil.getNewUuid();
        final String urn = UuidUtil.getTenantUrnFromUuid(id);
        final TenantEntityToTenantResponseConverter converter = new TenantEntityToTenantResponseConverter();

        TenantEntity entity = TenantEntity.builder()
            .active(active)
            .name(name)
            .id(id)
            .build();

        TenantResponse TenantResponse = converter.convert(entity);

        assertEquals(active, TenantResponse.getActive());
        assertEquals(name, TenantResponse.getName());
        assertEquals(urn, TenantResponse.getUrn());
    }
}
