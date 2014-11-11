package org.poscomp.xp.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by dmilne on 25/06/2014.
 */
@Configuration
@EnableMongoRepositories(basePackages = {"org.poscomp.xp.repository"})
@PropertySource("classpath:mongo.properties")
public class MongoConfig extends AbstractMongoConfiguration
{

    @Autowired
    Environment env;

    @Override
    protected String getDatabaseName() {
        return env.getProperty("mongo.databaseName");
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new MongoClient(env.getProperty("mongo.host"));
    }

}
