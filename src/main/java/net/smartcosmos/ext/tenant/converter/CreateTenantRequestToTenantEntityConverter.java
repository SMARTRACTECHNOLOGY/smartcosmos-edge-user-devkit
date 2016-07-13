package net.smartcosmos.ext.tenant.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.ext.tenant.domain.TenantEntity;
import net.smartcosmos.ext.tenant.dto.CreateTenantRequest;
import net.smartcosmos.ext.tenant.util.UuidUtil;
/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class CreateTenantRequestToTenantEntityConverter
    implements Converter<CreateTenantRequest, TenantEntity>, FormatterRegistrar {

    @Override
    public TenantEntity convert(CreateTenantRequest createTenantRequest) {
        return TenantEntity.builder()
            .id(UuidUtil.getNewUuid())
            .name(createTenantRequest.getName())
            .active(createTenantRequest.getActive())
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
