package net.smartcosmos.usermanagement.role.persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;

import net.smartcosmos.cluster.userdetails.domain.AuthorityEntity;
import net.smartcosmos.cluster.userdetails.domain.RoleEntity;
import net.smartcosmos.cluster.userdetails.repository.RoleRepository;
import net.smartcosmos.cluster.userdetails.util.UuidUtil;
import net.smartcosmos.usermanagement.role.dto.RoleRequest;
import net.smartcosmos.usermanagement.role.dto.RoleResponse;
import net.smartcosmos.usermanagement.role.exception.RoleAlreadyExistsException;
import net.smartcosmos.usermanagement.role.repository.AuthorityRepository;

/**
 * The JPA persistence implementation for Roles.
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
    public Optional<RoleResponse> createRole(String tenantUrn, RoleRequest createRoleRequest)
        throws ConstraintViolationException {

        // This role already exists? we're not creating a new one
        if (roleRepository.findByTenantIdAndNameIgnoreCase(UuidUtil.getUuidFromUrn(tenantUrn), createRoleRequest.getName())
            .isPresent()) {
            return Optional.empty();
        }

        Set<AuthorityEntity> authorityEntities = new HashSet<>();

        for (String authority : createRoleRequest.getAuthorities()) {
            authorityEntities.add(authorityRepository.save(AuthorityEntity.builder()
                                                               .authority(authority)
                                                               .build()));
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
    public Optional<RoleResponse> updateRole(String tenantUrn, String urn, RoleRequest updateRoleRequest)
        throws ConstraintViolationException, RoleAlreadyExistsException {

        findRoleByName(tenantUrn, updateRoleRequest.getName()).ifPresent(roleResponse -> {
            if (!urn.equalsIgnoreCase(roleResponse.getUrn())) {
                throw new RoleAlreadyExistsException(tenantUrn, updateRoleRequest.getName());
            }
        });

        UUID tenantId = UuidUtil.getUuidFromUrn(tenantUrn);
        UUID id = UuidUtil.getUuidFromUrn(urn);

        // Cancel update if role doesn't exist
        if (roleRepository.findByTenantIdAndId(tenantId, id)
            .isPresent()) {

            Set<AuthorityEntity> authorityEntities = new HashSet<>();
            for (String authority : updateRoleRequest.getAuthorities()) {
                authorityEntities.add(authorityRepository.save(AuthorityEntity.builder()
                                                                   .authority(authority)
                                                                   .build()));
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
    private <S, T> List<T> convertList(List<S> list, Class sourceClass, Class targetClass) {

        TypeDescriptor sourceDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(sourceClass));
        TypeDescriptor targetDescriptor = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(targetClass));

        return (List<T>) conversionService.convert(list, sourceDescriptor, targetDescriptor);
    }
}

