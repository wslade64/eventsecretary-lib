package au.com.eventsecretary.apps;

import au.com.eventsecretary.client.NotificationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
public class AppsConfiguration {
    @Bean
    public Notifier notifier(@Value("${supportEmailAddress}") String supportEmailAddress
             , @Value("${officeEmailAddress}") String officeEmailAddress
            , NotificationClient notificationClient) {
        return new Notifier(supportEmailAddress, officeEmailAddress, notificationClient);
    }
}
