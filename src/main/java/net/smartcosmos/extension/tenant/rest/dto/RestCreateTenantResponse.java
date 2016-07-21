package net.smartcosmos.extension.tenant.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import net.smartcosmos.extension.tenant.dto.CreateOrUpdateUserResponse;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "version" })
@Builder
@AllArgsConstructor
public class RestCreateTenantResponse {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private final String urn;
    private final String name;
    private final Boolean active;

    private final CreateOrUpdateUserResponse admin;
}
