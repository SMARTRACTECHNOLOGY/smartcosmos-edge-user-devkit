package net.smartcosmos.extension.tenant.converter;

import net.smartcosmos.extension.tenant.domain.TenantEntity;
import net.smartcosmos.extension.tenant.dto.UpdateTenantResponse;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TenantEntityToUpdateTenantResponseConverterTest {

    @Test
    public void thatConversionSucceeds() {
        final boolean active = true;
        final String name = "tenant_name";
        final UUID id = UuidUtil.getNewUuid();
        final String urn = UuidUtil.getTenantUrnFromUuid(id);
        final TenantEntityToUpdateTenantResponseConverter converter = new TenantEntityToUpdateTenantResponseConverter();

        TenantEntity entity = TenantEntity.builder()
                .active(active)
                .name(name)
                .id(id)
                .build();

        UpdateTenantResponse updateTenantResponse = converter.convert(entity);

        assertEquals(active, updateTenantResponse.getActive());
        assertEquals(name, updateTenantResponse.getName());
        assertEquals(urn, updateTenantResponse.getUrn());
    }
}
