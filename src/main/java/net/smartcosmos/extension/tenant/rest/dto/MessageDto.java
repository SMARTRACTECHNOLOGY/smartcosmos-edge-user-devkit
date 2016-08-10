package net.smartcosmos.extension.tenant.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for REST code/message responses.
 */
@JsonIgnoreProperties({ "version" })
@Data
@Builder
@AllArgsConstructor
public class MessageDto {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private final Integer code;
    private final String message;
}
