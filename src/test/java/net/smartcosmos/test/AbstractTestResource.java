package net.smartcosmos.test;

import java.io.IOException;
import java.util.Arrays;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import net.smartcosmos.usermanagement.DevKitUserManagementService;
import net.smartcosmos.usermanagement.role.persistence.RoleDao;
import net.smartcosmos.usermanagement.tenant.persistence.TenantDao;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Unit Testing sample for creating Tenants and Users.
 */
@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { DevKitUserManagementService.class })
public abstract class AbstractTestResource {

    protected MediaType contentType = MediaType.APPLICATION_JSON_UTF8;
    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;
    protected HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters)
            .stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .get();

        Assert.assertNotNull("the JSON message converter must not be null",
                             this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {

        MockitoAnnotations.initMocks(getClass());

        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    protected String json(Object o) throws IOException {

        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON,
                                                       mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Configuration
    static class ContextConfiguration {

        @Bean
        public TenantDao tenantDao() {

            return (Mockito.mock(TenantDao.class));
        }

        @Bean
        public RoleDao roleDao() {

            return (Mockito.mock(RoleDao.class));
        }
    }
}