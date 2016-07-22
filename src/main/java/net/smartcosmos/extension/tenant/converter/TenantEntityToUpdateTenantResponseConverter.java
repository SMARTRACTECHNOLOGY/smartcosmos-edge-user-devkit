package net.smartcosmos.extension.tenant.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.domain.TenantEntity;
import net.smartcosmos.extension.tenant.dto.tenant.UpdateTenantResponse;
import net.smartcosmos.extension.tenant.util.UuidUtil;

@Component
public class TenantEntityToUpdateTenantResponseConverter
    implements Converter<TenantEntity, UpdateTenantResponse>, FormatterRegistrar {

    @Override
    public UpdateTenantResponse convert(TenantEntity entity) {
        return UpdateTenantResponse.builder()
            .active(entity.getActive())
            .name(entity.getName())
            .urn(UuidUtil.getTenantUrnFromUuid(entity.getId()))
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(this);
    }
}
