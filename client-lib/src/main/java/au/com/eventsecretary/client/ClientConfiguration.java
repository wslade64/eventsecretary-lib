package au.com.eventsecretary.client;

import au.com.eventsecretary.accounting.account.CatalogueImpl;
import au.com.eventsecretary.equestrian.event.EventImpl;
import au.com.eventsecretary.equestrian.organisation.HorseImpl;
import au.com.eventsecretary.equestrian.organisation.RiderImpl;
import au.com.eventsecretary.security.Encryption;
import au.com.eventsecretary.user.access.AccessCode;
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
    public EquestrianAssociationClient equestrianAssociationClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new EquestrianAssociationClient(baseUrl, restTemplateBuilder);
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
    public ResourceClient<AccessCode> accessCodeClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
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
    public LeaderboardClient leaderboardClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        return new LeaderboardClient(baseUrl, restTemplateBuilder);
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

    // These keys are not a pair
    private final static String publicKeyValue = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqYEUEWRGqERxU4R2CXIe1g6vHkqQHjd6hNEkZITG72IGxF1seInbMSJ3nphADpnPiOIT9srsDGGQ2nOyPaPZUSbiN/tGw1aA/UO8o8hRDariAWKlpRE5oWpd5DPzeR/V8ZNHBIHO5EUnzKbovCnClGtkTg1Oo9IwDhweW72N87/CrgM7zNworJmL925wH0weZku9NUN7nPqzIeqK9dft5flRAqazej5WBycg8GENJIA/PlI9QwH1Prpjd5DaQca5krHrYlEhQAVPsUPS9pesmRxWJg655iewgy4v66AB/0PmXhI9M9mOsJ/d9H5xaDpKX0icT4QOyWYxeTniezg28wIDAQAB";
    private final static String privateKeyValue = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDOSslwuxCIYmWitC4dx6+u53EEaNFVGtGSCXjsESYzyK7UQXF7JUgzIOAl65dva+sB/gHqkuYPzEhxnKtqC2wKY4Z9L2fRri0c2T9nrjeO/eNpMFSxNpQBOf9hxJnBlav+hFT81IsmCPhNvafUrOSHDhy1pcM0p5b4RraHEZJ+uZvoyOP4gWj7J/fO+fkBQf5vDywgDPdkWXuOUYMH/8XnTdUFwkLiVQwwHOcMb5cSRn40RNFaYDkbSLRQkJ4pTLKVGNPTI+ZuUSV/+EgNi6hic2b+5x9s3kDXQrVzBZqxPQc6PPS40wRhskc/storbnr4mbVmKM12MdB/Nm+Pc3zpAgMBAAECggEABy6atXOzJKcVq294d42fM94CP5637a2XBELo1IHYVvndVFh0RKRMpN230A9+teCyTYnJXqBUpXzS54Rb12pPDaGKFMuqDVEcvaIL+hEaWr5uGhhrqmvLBVaT6enNV0GzEOkMiGzh7KYSoapBBeorCU9YIomDMqRvC3UddQsLPaF/7VuecjcDJzAhphDmKnASgugeM/XkaLMYFvUYfmhN43KCfEZl898IKciLiiBVNweOJzR5YWmcF4Bt3bdcsmFsYyAUlpW2EAd+Jb/NrSjR1dsEx8C0GmoxOtDA7+dljQp4VFcGd0HYUf/Z6iKvMtzNDgJ4PdQj3f2w2Invdis9rQKBgQD7BVVG4Vv8N0S05zrU6QNFf/6f+67adWfkRFlF5J0kxbcjvOAken65ge8j8kVMkPcTqKeZ/iC9oEhvRJ7CUUAwxjGk8qqO+Me3SOXlzc3q2FA7Ja9Ayf9r0OaC1cx4ZWlg/VyqV91aCU9Np7tiPkmErTBYM1g3qNCS6NoPUSoxxwKBgQDSYlMPg6pp8A/luIY68giJQHNFHIpaRu0DtFnDfuy2ehGYU3Foiejxr8hCyKYCBYGMazsDnLBCnuIyVJ+4awQBinv0DCkVlxjDYlk+34f/b8l9l5o/JrVIoELeWp1QUsfdRhyGj3MmpDa0G1JP2PLbIFfeyrn0hUki27ljfEDbzwKBgAp/pZpqUIOS+C+swBwphraAbPDaAO0oHelBJo6yyhPApTE8dA9yhBxUH+Fb/6P9KChNVH0vUMU9j+n/b7hm141yrJ7ee12xcADtbpgW7HbXBzwCDntiN2mV+h4cvQmIDEk/L4H8XMioPQXdbwvj2QVQjQYTV6w0Rkzuqjgbx+z/AoGBALxg4jLQGHVHSVTOxxJcyhvro8tMZO20+GEKyh1stxQldT/J55BNfFF8rnpuI4dH2toa4fq4AHOGxM+ASovc9vdSQu8hPGBirP+2Pmc3zBSCyg41Ax5s0II5Tea/iID1vpR5RO2P4PHGtFzM4gUxIzPmYAfTynISLoyR5gwYpBKtAoGANApEm2yrPWkPDv+CDeFo1cC9oXSvtP0AGgKOAqj5DJNGtVX+v12LH2NPLBlP7MIltUzUjJqma81B0hji0CUq/lKg6FZFgJsZJlvq2UEMJvcRQlC0/OIhWYPjTQTLy7nQS2DFwZIjsmRsZDU3b0G/axDtpPb9tJC2Croad8ptkKA=";

    @Bean
    public Encryption encryption() {
        return new Encryption(publicKeyValue, privateKeyValue);
    }
}
