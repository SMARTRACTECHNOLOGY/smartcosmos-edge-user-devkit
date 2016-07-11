package net.smartcosmos.ext.tenant.domain;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = { "username", "tenantId" }))
@EntityListeners({ AuditingEntityListener.class })
public class UserEntity implements Serializable {

    private static final int UUID_LENGTH = 16;
    private static final int STRING_FIELD_LENGTH = 255;
    private static final int BIG_STRING_FIELD_LENGTH = 767;

    /*
        Without setting an appropriate Hibernate naming strategy, the column names specified in the @Column annotations below will be converted
        from camel case to underscore, e.g.: systemUuid -> system_uuid

        To avoid that, select the "old" naming strategy org.hibernate.cfg.EJB3NamingStrategy in your configuration (smartcosmos-ext-objects-rdao.yml):
        jpa.hibernate.naming_strategy: org.hibernate.cfg.EJB3NamingStrategy
     */

    @Id
    @Type(type = "uuid-binary")
    @Column(name = "id", length = UUID_LENGTH)
    private UUID id;

    @NotNull
    @Type(type = "uuid-binary")
    @Column(name = "tenantid", length = UUID_LENGTH, nullable = false, updatable = false)
    private UUID tenantId;

    @NotEmpty
    @Size(max = STRING_FIELD_LENGTH)
    @Column(name = "username", length = STRING_FIELD_LENGTH, nullable = false, updatable = true)
    private String username;

    @NotEmpty
    @Size(max = STRING_FIELD_LENGTH)
    @Column(name = "emailaddress", length = STRING_FIELD_LENGTH, nullable = false, updatable = true)
    private String emailAddress;

    @Size(max = STRING_FIELD_LENGTH)
    @Column(name = "givenName", length = STRING_FIELD_LENGTH, nullable = true, updatable = true)
    private String givenName;

    @Size(max = STRING_FIELD_LENGTH)
    @Column(name = "surname", length = STRING_FIELD_LENGTH, nullable = true, updatable = true)
    private String surname;

    @NotEmpty
    @Size(max = STRING_FIELD_LENGTH)
    @Column(name = "password", length = STRING_FIELD_LENGTH, nullable = false, updatable = true)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
               joinColumns = { @JoinColumn(name = "user") },
               inverseJoinColumns = { @JoinColumn(name = "role") })
    private Set<RoleEntity> roles;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", insertable = true, updatable = false)
    private Date created;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastModified", nullable = false, insertable = true, updatable = true)
    private Date lastModified;

    @Basic
    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    /*
        Lombok's @Builder is not able to deal with field initialization default values. That's a known issue which won't get fixed:
        https://github.com/rzwitserloot/lombok/issues/663

        We therefore provide our own AllArgsConstructor that is used by the generated builder and takes care of field initialization.
     */
    @Builder
    @ConstructorProperties({ "id", "tenantId", "name", "username", "emailAddress", "givenName", "surname", "password", "roles",
                             "created", "lastModified", "active" })
    protected UserEntity(
        UUID id,
        UUID tenantId,
        String username,
        String emailAddress,
        String givenName,
        String surname,
        String password,
        Set<RoleEntity> roles,
        Date created,
        Date lastModified,
        Boolean active) {

        this.id = id;
        this.tenantId = tenantId;
        this.username = username;
        this.emailAddress = emailAddress;
        this.givenName = givenName;
        this.surname = surname;
        this.password = password;
        this.roles = roles;
        this.created = created;
        this.lastModified = lastModified;
        this.active = active != null ? active : true;
    }
}
