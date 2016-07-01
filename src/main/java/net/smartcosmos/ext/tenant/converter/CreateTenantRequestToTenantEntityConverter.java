package net.smartcosmos.ext.tenant.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;

import net.smartcosmos.ext.tenant.domain.TenantEntity;
import net.smartcosmos.ext.util.UuidUtil;
import net.smartcosmos.ext.tenant.dto.CreateTenantRequest;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public class CreateTenantRequestToTenantEntityConverter
    implements Converter<CreateTenantRequest, TenantEntity>, FormatterRegistrar {

    @Override
    public TenantEntity convert(CreateTenantRequest createTenantRequest) {
        return TenantEntity.builder()
            .id(UuidUtil.getNewUuid())
            .name(createTenantRequest.getName())
            .active(createTenantRequest.getActive())
            .username(createTenantRequest.getUsername())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
