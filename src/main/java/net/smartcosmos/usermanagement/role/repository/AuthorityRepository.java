package net.smartcosmos.usermanagement.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.smartcosmos.cluster.userdetails.domain.AuthorityEntity;

/**
 * The Spring Data JPA persistence definition for Role Authorities.
 */
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, String>,
                                             PagingAndSortingRepository<AuthorityEntity, String>,
                                             JpaSpecificationExecutor<AuthorityEntity> {

}
