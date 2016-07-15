package net.smartcosmos.extension.tenant.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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

import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toList;

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
    public Optional<CreateOrUpdateRoleResponse> updateRole (String tenantUrn, String urn, CreateOrUpdateRoleRequest updateRoleRequest)
        throws ConstraintViolationException {

        UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
        UUID id = UuidUtil.getUuidFromUrn(urn);

        // Cancel update if role doesn't exist
        if (roleRepository.findByIdAndTenantId(id, tenantId).isPresent()) {

            Set<AuthorityEntity> authorityEntities = new HashSet<>();
            for (String authority : updateRoleRequest.getAuthorities()) {
                authorityEntities.add(authorityRepository.save(AuthorityEntity.builder().authority(authority).build()));
            }

            RoleEntity role = roleRepository.save(RoleEntity.builder()
                    .id(id)
                    .tenantId(tenantId)
                    .name(updateRoleRequest.getName())
                    .authorities(authorityEntities)
                    .active(updateRoleRequest.getActive())
                    .build());
            return Optional.ofNullable(conversionService.convert(role, CreateOrUpdateRoleResponse.class));
        }

        return Optional.empty();
    }

    public Optional<GetRoleResponse> findByTenantUrnAndName(String tenantUrn, String name) {
        Optional<RoleEntity> roleEntity = roleRepository.findByNameAndTenantId(name, UuidUtil.getUuidFromUrn(tenantUrn));
        if (roleEntity.isPresent()) {
            return Optional.ofNullable(conversionService.convert(roleEntity.get(), GetRoleResponse.class));
        }
        return Optional.empty();
    }

    public Optional<RoleEntity> findByUrnAsEntity(String urn) {
        return roleRepository.findById(UuidUtil.getUuidFromUrn(urn));
    }

    @Override
    @Transactional
    public List<GetRoleResponse> delete(String tenantUrn, String urn)
            throws IllegalArgumentException {

        List<RoleEntity> roleEntities = roleRepository
                .deleteByIdAndTenantId(UuidUtil.getUuidFromUrn(urn), UuidUtil.getUuidFromUrn(tenantUrn));
        return roleEntities
                .stream()
                .map(item -> conversionService.convert(item, GetRoleResponse.class))
                .collect(toList());
    }
}

