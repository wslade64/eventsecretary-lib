package au.com.eventsecretary.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Bean
    public IdentityClient identityClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new IdentityClient(baseUrl, restTemplateBuilder);
    }

    @Bean
    public PaymentClient paymentClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new PaymentClient(baseUrl, restTemplateBuilder);
    }

    @Bean
    public PeopleClient peopleClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new PeopleClient(baseUrl, restTemplateBuilder);
    }
}
