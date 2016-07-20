package net.smartcosmos.extension.tenant.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestAuthenticateRequest {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private String username;
    private String password;
}
