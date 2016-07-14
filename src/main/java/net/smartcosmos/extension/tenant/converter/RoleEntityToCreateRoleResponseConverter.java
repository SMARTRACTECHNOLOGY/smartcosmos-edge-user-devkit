package net.smartcosmos.extension.tenant.converter;

import java.util.ArrayList;
import java.util.List;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.extension.tenant.util.UuidUtil;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Component
public class RoleEntityToCreateRoleResponseConverter
    implements Converter<RoleEntity, CreateOrUpdateRoleResponse>, FormatterRegistrar {

    @Override
    public CreateOrUpdateRoleResponse convert(RoleEntity roleEntity) {

        // role entities from role strings
        List<String> authorities = new ArrayList<>();
        for (AuthorityEntity authorityEntity: roleEntity.getAuthorities()) {
            authorities.add(authorityEntity.getAuthority());
        }


        return CreateOrUpdateRoleResponse.builder()
            .urn(UuidUtil.getUserUrnFromUuid(roleEntity.getId()))
            .name(roleEntity.getName())
            .active(roleEntity.getActive())
            .authorities(authorities)
            .build();
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {

        registry.addConverter(this);
    }
}
