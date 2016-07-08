package net.smartcosmos.ext.tenant;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import net.smartcosmos.spring.EnableSmartCosmos;
import net.smartcosmos.spring.EnableSmartCosmosExtension;
import net.smartcosmos.spring.EnableSmartCosmosSecurity;

@EnableJpaRepositories
@EnableJpaAuditing
@EntityScan
@ComponentScan
@Configuration
@EnableSmartCosmosExtension
@EnableSmartCosmos
@EnableSmartCosmosSecurity
public class TenantRdao extends WebMvcConfigurerAdapter {

    protected HttpMessageConverter mappingJackson2HttpMessageConverter;

    public static void main(String[] args) {
        new SpringApplicationBuilder(TenantRdao.class).web(true).run(args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Autowired
    Map<String, FormatterRegistrar> formatterRegistrarMap;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        for (FormatterRegistrar registrar : formatterRegistrarMap.values()) {
            registrar.registerFormatters(registry);
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setOneIndexedParameters(true);
        argumentResolvers.add(resolver);
        super.addArgumentResolvers(argumentResolvers);
    }

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny().get();
    }


}
