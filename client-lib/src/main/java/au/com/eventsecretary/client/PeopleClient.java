package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.people.Person;
import au.com.eventsecretary.people.presentation.PersonIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author sladew
 */
@Component
public class PeopleClient {
    private static final String URI = "/user/v1/people";

    Logger logger = LoggerFactory.getLogger(PeopleClient.class);
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public PeopleClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        this.baseUrl = baseUrl;
        restTemplate = restTemplateBuilder.build();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                switch (clientHttpResponse.getStatusCode()) {
                    case NOT_FOUND:
                        throw new ResourceNotFoundException("Identity not found");
                    case CONFLICT:
                        logger.info("login:" + clientHttpResponse.getStatusCode());
                        throw new ResourceExistsException("Email address is already registered.");
                    case UNAUTHORIZED:
                        logger.info("login:" + clientHttpResponse.getStatusCode());
                        throw new UnauthorizedException();
                    default:
                        super.handleError(clientHttpResponse);
                }
            }
        });
    }

    private static <T> HttpEntity<T> createEntity() {
        HttpHeaders headers = new HttpHeaders();
        String bearer = MDC.get(SecurityInterceptor.BEARER_KEY);
        if (bearer != null) {
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(bearer));
        }
        return new HttpEntity<>(headers);
    }

    private static <T> HttpEntity<T> createEntityBody(T body) {
        HttpHeaders headers = new HttpHeaders();
        String bearer = MDC.get(SecurityInterceptor.BEARER_KEY);
        if (bearer != null) {
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(bearer));
        }
        return new HttpEntity<>(body, headers);
    }


    public Person getPerson(String personId) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Person> exchange = restTemplate.exchange(baseUrl + URI + "/person/" + personId, HttpMethod.GET, httpEntity, Person.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to person service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Person findPersonByIdentityId(String identityId) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Person[]> exchange = restTemplate.exchange(baseUrl + URI + "/person", HttpMethod.GET, httpEntity, Person[].class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody()[0];
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to person service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public PersonIdentity findPersonIdentityByIdentityId(String identityId) {
        try {
            ResponseEntity<PersonIdentity> exchange = restTemplate.getForEntity(baseUrl + URI + "/personIdentity/" + identityId, PersonIdentity.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void reset() {
        try {
            HttpEntity<PersonIdentity> httpEntity = createEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/reset", HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("reset:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public PersonIdentity createPerson(PersonIdentity personIdentity) {
        try {
            HttpEntity<PersonIdentity> httpEntity = createEntityBody(personIdentity);

            ResponseEntity<PersonIdentity> exchange = restTemplate.exchange(baseUrl + URI + "/personIdentity", HttpMethod.POST, httpEntity, PersonIdentity.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    // List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public PersonIdentity updatePersonIdentity(PersonIdentity personIdentity) {
        try {
            HttpEntity<PersonIdentity> httpEntity = createEntityBody(personIdentity);

            ResponseEntity<PersonIdentity> exchange = restTemplate.exchange(baseUrl + URI + "/personIdentity", HttpMethod.PUT, httpEntity, PersonIdentity.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("updatePersonIdentity:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public String createPerson(Person person) {
        try {
            HttpEntity<Person> httpEntity = createEntityBody(person);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/person", HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return locationHeader.get(0);
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("createPerson:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void updatePerson(Person person) {
        try {
            HttpEntity<Person> httpEntity = createEntityBody(person);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/person", HttpMethod.PUT, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("updatePerson:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Person getPersonByNames(String firstName, String lastName) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Person[]> exchange = restTemplate.exchange(baseUrl + URI + "/person?" + "firstName=" + firstName + "&lastName=" + lastName, HttpMethod.GET, httpEntity, Person[].class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody().length == 0 ? null : exchange.getBody()[0];
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("getPersonByNames:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public List<Person> findPeople(String query) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Person[]> exchange = restTemplate.exchange(baseUrl + URI + "/person?" + "query=" + query, HttpMethod.GET, httpEntity, Person[].class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return Arrays.asList(exchange.getBody());
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("findPeople:could not connect to person service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
