package net.smartcosmos.ext.tenant;

import java.util.List;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import net.smartcosmos.spring.EnableSmartCosmos;
import net.smartcosmos.spring.EnableSmartCosmosExtension;
import net.smartcosmos.spring.EnableSmartCosmosSecurity;

@ComponentScan
@EnableSmartCosmosExtension
@EnableSmartCosmos
@EnableSmartCosmosSecurity
public class TenantRdao extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(TenantRdao.class).web(true).run(args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setOneIndexedParameters(true);
        argumentResolvers.add(resolver);
        super.addArgumentResolvers(argumentResolvers);
    }

}
