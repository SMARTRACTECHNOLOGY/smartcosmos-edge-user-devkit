package net.smartcosmos.extension.tenant.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.smartcosmos.extension.tenant.domain.RoleEntity;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface RoleRepository extends JpaRepository<RoleEntity, String>,
                                        PagingAndSortingRepository<RoleEntity, String>,
                                        JpaSpecificationExecutor<RoleEntity> {

    Optional<RoleEntity> findByNameAndTenantId(String name, UUID tenantId);

    Optional<RoleEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<RoleEntity> findById(UUID id);

    List<RoleEntity> deleteByIdAndTenantId(UUID id, UUID tenantId);
}
