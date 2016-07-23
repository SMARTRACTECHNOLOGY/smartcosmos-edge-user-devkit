package net.smartcosmos.extension.tenant.impl;

import lombok.extern.slf4j.Slf4j;
import net.smartcosmos.extension.tenant.dao.RoleDao;
import net.smartcosmos.extension.tenant.domain.AuthorityEntity;
import net.smartcosmos.extension.tenant.domain.RoleEntity;
import net.smartcosmos.extension.tenant.dto.role.CreateOrUpdateRoleRequest;
import net.smartcosmos.extension.tenant.dto.role.RoleResponse;
import net.smartcosmos.extension.tenant.repository.AuthorityRepository;
import net.smartcosmos.extension.tenant.repository.RoleRepository;
import net.smartcosmos.extension.tenant.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.*;

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
    public Optional<RoleResponse> createRole(String tenantUrn, CreateOrUpdateRoleRequest createRoleRequest)
        throws ConstraintViolationException {

        // This role already exists? we're not creating a new one
        if (roleRepository.findByTenantIdAndNameIgnoreCase(UuidUtil.getUuidFromUrn(tenantUrn), createRoleRequest.getName())
            .isPresent()) {
            return Optional.empty();
        }

        Set<AuthorityEntity> authorityEntities = new HashSet<>();

        for (String authority : createRoleRequest.getAuthorities()) {
            authorityEntities.add(authorityRepository.save(AuthorityEntity.builder().authority(authority).build()));
        }

        RoleEntity role = roleRepository.save(RoleEntity.builder()
                                                  .id(UuidUtil.getNewUuid())
                                                  .tenantId(UuidUtil.getUuidFromUrn(tenantUrn))
                                                  .name(createRoleRequest.getName())
                                                  .authorities(authorityEntities)
                                                  .active(createRoleRequest.getActive())
                                                  .build());
        return Optional.ofNullable(conversionService.convert(role, RoleResponse.class));
    }

    @Override
    public Optional<RoleResponse> updateRole(String tenantUrn, String urn, CreateOrUpdateRoleRequest updateRoleRequest)
        throws ConstraintViolationException {

        UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
        UUID id = UuidUtil.getUuidFromUrn(urn);

        // Cancel update if role doesn't exist
        if (roleRepository.findByTenantIdAndId(tenantId, id).isPresent()) {

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
            return Optional.ofNullable(conversionService.convert(role, RoleResponse.class));
        }

        return Optional.empty();
    }

    public Optional<RoleResponse> findRoleByName(String tenantUrn, String name) {
        Optional<RoleEntity> roleEntity = roleRepository.findByTenantIdAndNameIgnoreCase(UuidUtil.getUuidFromUrn(tenantUrn), name);
        if (roleEntity.isPresent()) {
            return Optional.ofNullable(conversionService.convert(roleEntity.get(), RoleResponse.class));
        }
        return Optional.empty();
    }

    public Optional<RoleEntity> findByUrnAsEntity(String tenantUrn, String urn) {
        return roleRepository.findByTenantIdAndId(UuidUtil.getUuidFromUrn(tenantUrn), UuidUtil.getUuidFromUrn(urn));
    }

    @Override
    @Transactional
    public List<RoleResponse> delete(String tenantUrn, String urn)
        throws IllegalArgumentException {

        List<RoleEntity> roleEntities = roleRepository
            .deleteByTenantIdAndId(UuidUtil.getUuidFromUrn(tenantUrn), UuidUtil.getUuidFromUrn(urn));
        return convertList(roleEntities, RoleEntity.class, RoleResponse.class);
    }

    @Override
    public List<RoleResponse> findAllRoles(String tenantUrn) {

        UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
        List<RoleEntity> roleEntities = roleRepository.findByTenantId(tenantId);
        return convertList(roleEntities, RoleEntity.class, RoleResponse.class);
    }

    @Override
    public Optional<RoleResponse> findRoleByUrn(String tenantUrn, String urn) {

        UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
        UUID roleId = UuidUtil.getUuidFromUrn(urn);

        Optional<RoleEntity> roleEntity = roleRepository.findByTenantIdAndId(tenantId, roleId);
        if (roleEntity.isPresent()) {
            return Optional.ofNullable(conversionService.convert(roleEntity.get(), RoleResponse.class));
        }
        return Optional.empty();
    }

    /**
     * Uses the conversion service to convert a typed list into another typed list.
     *
     * @param list the list
     * @param sourceClass the class of the source type
     * @param targetClass the class of the target type
     * @param <S> the generic source type
     * @param <T> the generic target type
     * @return the converted typed list
     */
    @SuppressWarnings("unchecked")
    private <S, T> List<T> convertList(List<S> list, Class sourceClass, Class targetClass) {

        TypeDescriptor sourceDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(sourceClass));
        TypeDescriptor targetDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(targetClass));

        return (List<T>) conversionService.convert(list, sourceDescriptor, targetDescriptor);
    }
}

