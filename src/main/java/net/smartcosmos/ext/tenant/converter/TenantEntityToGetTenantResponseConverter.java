package net.smartcosmos.ext.tenant.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.ext.tenant.domain.TenantEntity;
import net.smartcosmos.ext.tenant.dto.GetTenantResponse;
import net.smartcosmos.ext.tenant.util.UuidUtil;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class TenantEntityToGetTenantResponseConverter
    implements Converter<TenantEntity, GetTenantResponse>, FormatterRegistrar {

    @Override
    public GetTenantResponse convert(TenantEntity tenantEntity) {

        if (tenantEntity == null) {
            return null;
        }

        return GetTenantResponse.builder()
            .name(tenantEntity.getName())
            .active(tenantEntity.getActive())
            .urn(UuidUtil.getTenantUrnFromUuid(tenantEntity.getId()))
            .build();
    }
    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }

}
