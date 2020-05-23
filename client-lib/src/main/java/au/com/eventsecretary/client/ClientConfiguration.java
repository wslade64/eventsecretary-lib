package au.com.eventsecretary.client;

import au.com.eventsecretary.equestrian.event.EventImpl;
import au.com.eventsecretary.equestrian.organisation.HorseImpl;
import au.com.eventsecretary.equestrian.organisation.RiderImpl;
import au.com.eventsecretary.user.identity.AuthorisationImpl;
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
    public AccountClient accountClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new AccountClient(baseUrl, restTemplateBuilder);
    }

    @Bean
    public OrderClient orderClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new OrderClient(baseUrl, restTemplateBuilder);
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
    public RegistrationClient registrationClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new RegistrationClient(baseUrl, restTemplateBuilder);
    }

    @Bean
    public ResourceClient riderClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new ResourceClient(baseUrl + "/equestrian-ms/rider", restTemplateBuilder, RiderImpl.class);
    }

    @Bean
    public ResourceClient horseClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new ResourceClient(baseUrl + "/equestrian-ms/horse", restTemplateBuilder, HorseImpl.class);
    }

    @Bean
    public ResourceClient eventClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new ResourceClient(baseUrl + "/equestrian-ms/v1/event", restTemplateBuilder, EventImpl.class);
    }

    @Bean
    public AuthorisationClient authorisationClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new AuthorisationClient(baseUrl + "/user/v1/authorisation", restTemplateBuilder, AuthorisationImpl.class);
    }
}
