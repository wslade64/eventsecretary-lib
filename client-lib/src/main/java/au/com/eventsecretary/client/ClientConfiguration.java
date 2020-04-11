package au.com.eventsecretary.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Bean
    public NotificationClient notificationClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new NotificationClient(baseUrl, restTemplateBuilder);
    }

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

    @Bean
    public FacilityClient facilityClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new FacilityClient(baseUrl, restTemplateBuilder);
    }

    @Bean
    public AssociationClient associationClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new AssociationClient(baseUrl, restTemplateBuilder);
    }

    @Bean
    public OrganisationClient organisationClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new OrganisationClient(baseUrl, restTemplateBuilder);
    }

    @Bean
    public MembershipClient membershipClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new MembershipClient(baseUrl, restTemplateBuilder);
    }
}
