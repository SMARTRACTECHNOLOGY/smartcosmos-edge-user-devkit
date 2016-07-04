package net.smartcosmos.ext.tenant;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
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

    public static void main(String[] args) {
        new SpringApplicationBuilder(TenantRdao.class).web(true).run(args);
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
}
