package au.com.eventsecretary.dao;

import au.com.eventsecretary.persistence.BusinessObjectPersistence;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * @author sladew
 */
@Configuration
public class DbConfiguration {

    @Bean
    public MongoDbFactory mongoDbFactory(MongoProperties properties) throws UnknownHostException {
        ServerAddress serverAddress = new ServerAddress(properties.getHost(), properties.getPort());

        MongoClient mongoClient;

        if (StringUtils.isBlank(properties.getUsername())) {
            mongoClient = new MongoClient(serverAddress);
        } else {

            // Set credentials
            MongoCredential credential = MongoCredential.createCredential(
                    properties.getUsername(),
                    properties.getName(),
                    properties.getPassword().toCharArray());

            // Mongo Client
            mongoClient = new MongoClient(serverAddress, Arrays.asList(credential));
        }

        // Mongo DB Factory
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(
                mongoClient, properties.getName());

        return simpleMongoDbFactory;
    }

//    @Bean
//    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
//        return new MongoTemplate(mongoDbFactory);
//    }

    @Bean
    public BusinessObjectPersistence mongoBusinessObjectPersistence(MongoTemplate mongoTemplate) {
        return new MongoBusinessObjectPersistence(mongoTemplate);
    }
}
