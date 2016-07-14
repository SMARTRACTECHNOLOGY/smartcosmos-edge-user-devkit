package net.smartcosmos.extension.tenant.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.extension.tenant.dto.GetRoleResponse;
import net.smartcosmos.extension.tenant.repository.AuthorityRepository;
import net.smartcosmos.extension.tenant.repository.RoleRepository;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.CreateOrUpdateRoleResponse;
import net.smartcosmos.extension.tenant.dto.GetRoleResponse;
import net.smartcosmos.extension.tenant.repository.AuthorityRepository;
import net.smartcosmos.extension.tenant.repository.RoleRepository;
import net.smartcosmos.extension.tenant.util.UuidUtil;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
@Slf4j
@Service
public class RolePersistenceService implements RoleDao {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final ConversionService conversionService;

    @Autowired
    public RolePersistenceService(
        RoleRepository roleRepository,
        AuthorityRepository authorityRepository,
        ConversionService conversionService) {

        this.roleRepository = roleRepository;
        this.authorityRepository = authorityRepository;
        this.conversionService = conversionService;
    }

    @Override
    public Optional<CreateOrUpdateRoleResponse> createRole (String tenantUrn, CreateOrUpdateRoleRequest createRoleRequest)
        throws ConstraintViolationException {

        // This role already exists? we're not creating a new one
        if (roleRepository.findByNameAndTenantId(createRoleRequest.getName(), UuidUtil.getUuidFromUrn(tenantUrn))
            .isPresent()) {
            return Optional.empty();
        }

        Set<AuthorityEntity> authorityEntities = new HashSet<>();

        for (String authority: createRoleRequest.getAuthorities()) {
            authorityEntities.add(authorityRepository.save(AuthorityEntity.builder().authority(authority).build()));
        }

        RoleEntity role = roleRepository.save(RoleEntity.builder()
                                                  .id(UuidUtil.getNewUuid())
                                                  .tenantId(UuidUtil.getUuidFromUrn(tenantUrn))
                                                  .name(createRoleRequest.getName())
                                                  .authorities(authorityEntities)
                                                  .active(createRoleRequest.getActive())
                                                  .build());
        return Optional.ofNullable(conversionService.convert(role, CreateOrUpdateRoleResponse.class));
    }

    @Override
    public Optional<CreateOrUpdateRoleResponse> updateRole (String tenantUrn, CreateOrUpdateRoleRequest updateRoleRequest)
        throws ConstraintViolationException {

        // This role already exists? we're not creating a new one
        if (roleRepository.findById(UuidUtil.getUuidFromUrn(tenantUrn)).isPresent()) {
            return Optional.empty();
        }

        Set<AuthorityEntity> authorityEntities = new HashSet<>();
        for (String authority: updateRoleRequest.getAuthorities()) {
            authorityEntities.add(authorityRepository.save(AuthorityEntity.builder().authority(authority).build()));
        }

        RoleEntity role = roleRepository.save(RoleEntity.builder()
                                                  .id(UuidUtil.getUuidFromUrn(updateRoleRequest.getUrn()))
                                                  .tenantId(UuidUtil.getUuidFromUrn(tenantUrn))
                                                  .name("Admin")
                                                  .authorities(authorityEntities)
                                                  .active(true)
                                                  .build());
        return Optional.ofNullable(conversionService.convert(role, CreateOrUpdateRoleResponse.class));
    }

    public Optional<GetRoleResponse> findByNameAndTenantUrn(String name, String tenantUrn) {
        Optional<RoleEntity> roleEntity = roleRepository.findByNameAndTenantId(name, UuidUtil.getUuidFromUrn(tenantUrn));
        if (roleEntity.isPresent()) {
            return Optional.ofNullable(conversionService.convert(roleEntity, GetRoleResponse.class));
        }
        return Optional.empty();
    }
    public Optional<RoleEntity> findByUrnAsEntity(String urn) {
        return roleRepository.findById(UuidUtil.getUuidFromUrn(urn));
    }
}

