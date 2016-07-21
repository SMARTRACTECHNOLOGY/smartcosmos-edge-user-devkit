package net.smartcosmos.extension.tenant.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("smartcosmos.security.resource.user-details")
public class ServiceUserProperties {
    private String username;
    private String password;
}
