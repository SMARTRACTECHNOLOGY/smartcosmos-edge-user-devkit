package net.smartcosmos.ext.tenant.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.smartcosmos.ext.tenant.domain.RoleEntity;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface RoleRepository extends JpaRepository<RoleEntity, String>,
                                        PagingAndSortingRepository<RoleEntity, String>,
                                        JpaSpecificationExecutor<RoleEntity> {

    Optional<RoleEntity> findByNameAndTenantId(String name, UUID tenantId);

    Optional<RoleEntity> findById(UUID id);

}
