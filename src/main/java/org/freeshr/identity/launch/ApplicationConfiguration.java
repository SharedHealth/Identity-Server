package org.freeshr.identity.launch;

import org.freeshr.identity.repository.IdentityRepository;
import org.freeshr.identity.repository.IdentityRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2CollectionHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.IOException;
import java.util.List;

@Configuration
@ComponentScan("org.freeshr.identity")
public class ApplicationConfiguration extends WebMvcConfigurationSupport {
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
        converters.add(new Jaxb2CollectionHttpMessageConverter<>());
        super.configureMessageConverters(converters);
    }


}
