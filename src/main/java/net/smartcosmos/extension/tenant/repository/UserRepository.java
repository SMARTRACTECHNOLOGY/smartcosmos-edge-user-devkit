package net.smartcosmos.extension.tenant.repository;

import java.util.Optional;
import java.util.UUID;

import net.smartcosmos.extension.tenant.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.smartcosmos.extension.tenant.domain.UserEntity;

/**
 * Initially created by SMART COSMOS Team on June 30, 2016.
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID>,
                                        PagingAndSortingRepository<UserEntity, UUID>,
                                        JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByUsernameAndTenantId(String username, UUID tenantId);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

}
