package net.smartcosmos.dao.tenant.rest.resource;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import net.smartcosmos.dao.tenant.rest.dto.RestCreateTenantRequest;
import net.smartcosmos.dao.tenant.rest.service.CreateTenantService;
import net.smartcosmos.security.EndpointMethodControl;
import net.smartcosmos.spring.SmartCosmosRdao;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Initially created by SMART COSMOS Team on July 01, 2016.
 */
@SmartCosmosRdao
@Slf4j
@ConditionalOnProperty(prefix = "smt.endpoints.things", name = "enabled", matchIfMissing = true)
//@Api
public class CreateTenantResource {

    private CreateTenantService service;

    @Autowired
    public CreateTenantResource(CreateTenantService service) { this.service = service; }

    @RequestMapping(value = "/tenant", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    @EndpointMethodControl(key = "things.post")
    @ConditionalOnProperty(prefix = "smt.endpoints.things.post", name = "enabled", matchIfMissing = true)
    public DeferredResult<ResponseEntity> createObject(
        @RequestBody @Valid RestCreateTenantRequest restCreateTenantRequest) {

        return service.create(restCreateTenantRequest);
    }
}


