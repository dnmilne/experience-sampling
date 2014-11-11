package org.poscomp.xp.config;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.ordering.ResourceListingPositionalOrdering;
import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.wordnik.swagger.model.ApiInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpSession;

/**
 * Created by dmilne on 10/07/2014.
 */
@Configuration
@EnableSwagger
@PropertySource("classpath:swagger.properties")
public class SwaggerConfig {

    @Autowired
    Environment env ;

    private SpringSwaggerConfig swaggerConfig;

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig swaggerConfig) {
        this.swaggerConfig = swaggerConfig;
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {

        return new SwaggerSpringMvcPlugin(this.swaggerConfig)
                .ignoredParameterTypes(HttpSession.class)
                .apiListingReferenceOrdering(new ResourceListingPositionalOrdering())
                .pathProvider(new RelativeSwaggerPathProvider())
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                env.getProperty("swagger.api.title"),
                env.getProperty("swagger.api.description"),
                env.getProperty("swagger.api.terms"),
                env.getProperty("swagger.api.email"),
                env.getProperty("swagger.api.licence.type"),
                env.getProperty("swagger.api.licence.url")
        );
        return apiInfo;
    }
}
