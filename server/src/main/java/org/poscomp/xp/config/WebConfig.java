package org.poscomp.xp.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.poscomp.xp.util.IdFormatting;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dmilne on 25/06/2014.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"org.poscomp.xp"})
public class WebConfig extends WebMvcConfigurerAdapter {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z") ;

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {

        configurer.enable();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jsonConverter()) ;
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter() {

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter() ;

        converter.getObjectMapper()
                .setDateFormat(dateFormat) ;

        SimpleModule module = new SimpleModule();
        module.addSerializer(ObjectId.class, new IdFormatting.IdSerializer());
        module.addDeserializer(ObjectId.class, new IdFormatting.IdDeserializer()) ;
        converter.getObjectMapper().registerModule(module);

        return converter ;
    }

}
