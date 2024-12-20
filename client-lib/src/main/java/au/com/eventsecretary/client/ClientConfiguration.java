package au.com.eventsecretary.client;

import au.com.eventsecretary.accounting.account.CatalogueImpl;
import au.com.eventsecretary.equestrian.event.EventImpl;
import au.com.eventsecretary.equestrian.organisation.HorseImpl;
import au.com.eventsecretary.equestrian.organisation.RiderImpl;
import au.com.eventsecretary.user.access.AccessCodeImpl;
import au.com.eventsecretary.user.identity.ActivationImpl;
import au.com.eventsecretary.user.identity.AuthorisationImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
public class ClientConfiguration {
    @Bean
    public NotificationClient notificationClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new NotificationClient(baseUrl, restTemplateBuilder);
    }

    @Bean
    public NoteClient noteClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new NoteClient(baseUrl, restTemplateBuilder);
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
    @Qualifier("equestrian")
    public EventClient equestrianClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new EventClient(baseUrl, restTemplateBuilder);
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
    public ResourceClient entryClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new ResourceClient(baseUrl + "/equestrian-ms/v1/entry", restTemplateBuilder, EventImpl.class);
    }

    @Bean
    public ResourceClient catalogueClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new ResourceClient(baseUrl + "/payment/catalogue", restTemplateBuilder, CatalogueImpl.class);
    }

    @Bean
    public WwcClient wwcClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new WwcClient(baseUrl + "/user/v1/wwc", restTemplateBuilder);
    }

    @Bean
    public ResourceClient activationClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new ResourceClient(baseUrl + "/user/v2/activate", restTemplateBuilder, ActivationImpl.class);
    }

    @Bean
    public AuthorisationClient authorisationClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new AuthorisationClient(baseUrl + "/user/v1/authorisation", restTemplateBuilder, AuthorisationImpl.class);
    }

    @Bean
    public ResourceClient accessCodeClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new ResourceClient(baseUrl + "/user/v1/access-code", restTemplateBuilder, AccessCodeImpl.class);
    }

    @Bean
    public SessionService sessionService(AuthenticateClient authenticateClient) {
        return new SessionService(authenticateClient);
    }

    @Bean
    public AuthenticateClient authenticateClient(IdentityClient identityClient, AuthorisationClient authorisationClient, PeopleClient peopleClient) {
        return new AuthenticateClientDelegate(identityClient, authorisationClient, peopleClient);
    }

    @Bean
    public TagsClient tagsClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new TagsClient(baseUrl, restTemplateBuilder);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hrcav.validation", value = "systemToken")
    public ValidationClient hrcavValidationClient(@Value("${hrcav.validation.systemToken}") String systemToken, @Value("${hrcav.validation.url}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new ValidationClient(systemToken, baseUrl, restTemplateBuilder);
    }

    @Bean
    @ConditionalOnProperty(prefix = "hrcav.qualification", value = "systemToken")
    public QualificationClient hrcavQualificationClient(@Value("${hrcav.qualification.systemToken}") String systemToken, @Value("${hrcav.qualification.url}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new QualificationClient(systemToken, baseUrl, restTemplateBuilder);
    }
}
