package au.com.eventsecretary.dao;

import au.com.eventsecretary.persistence.BusinessObjectPersistence;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.Arrays;

/**
 * @author sladew
 */
@Configuration
public class DbConfiguration {

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoProperties properties) {
        ServerAddress serverAddress = new ServerAddress(properties.getHost(), properties.getPort());

        MongoClient mongoClient;

        if (StringUtils.isBlank(properties.getUsername())) {
            mongoClient = MongoClients.create(MongoClientSettings.builder()
                    .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(serverAddress)))
                    .build());
        } else {

            // Set credentials
            MongoCredential credential = MongoCredential.createCredential(
                    properties.getUsername(),
                    properties.getName(),
                    properties.getPassword().toCharArray());

            // Mongo Client
            mongoClient = MongoClients.create(MongoClientSettings.builder()
                    .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(serverAddress)))
                            .credential(credential)
                    .build());
        }

        // Mongo DB Factory
        SimpleMongoClientDatabaseFactory simpleMongoDatabaseFactory = new SimpleMongoClientDatabaseFactory(
                mongoClient, properties.getName());

        return simpleMongoDatabaseFactory;
    }

    @Bean
    public SequenceService sequenceService(BusinessObjectPersistence persistence) {
        return new SequenceService(persistence);
    }

    @Bean
    public LockedProcess lockedProcess() {
        return new LockedProcess();
    }

    @Bean
    public BusinessObjectPersistence mongoBusinessObjectPersistence(MongoTemplate mongoTemplate) {
        return new MongoBusinessObjectPersistence(mongoTemplate);
    }
}
