package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.user.identity.Identity;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sladew
 */
public class IdentityClient extends AbstractClient {
    private static final String URI = "/user/v1/identity";

    public IdentityClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public Identity findIdentity() {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Identity> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.GET, httpEntity, Identity.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to identity service" + e.getMessage());
            return null;
        }
    }

    public Identity findByEmailAddress(String emailAddress) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            Map<String, String> params = new HashMap<>();
            params.put("emailAddress", "=" + emailAddress);
            ResponseEntity<Identity> exchange = restTemplate.exchange(baseUrl + URI + "?email={emailAddress}", HttpMethod.GET, httpEntity, Identity.class, params);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException(emailAddress);
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to identity service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Identity findByExactEmailAddress(String emailAddress) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            Map<String, String> params = new HashMap<>();
            params.put("emailAddress", emailAddress);
            params.put("match", "exact");
            ResponseEntity<Identity> exchange = restTemplate.exchange(baseUrl + URI + "?email={emailAddress}&match={match}", HttpMethod.GET, httpEntity, Identity.class, params);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException(emailAddress);
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to identity service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Identity findById(String id) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Identity> exchange = restTemplate.exchange(baseUrl + URI + "/" + id, HttpMethod.GET, httpEntity, Identity.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException(id);
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("findById:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public String create(Identity userIdentity) {
        try {
            logger.info("create:" + userIdentity.getEmail());

            HttpEntity<Identity> httpEntity = createSystemEntityBody(userIdentity);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Void.class);
            switch (wrap(exchange.getStatusCode())) {
                case CREATED:
                    List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return locationHeader.get(0);
                case CONFLICT:
                    throw new ResourceExistsException("Email address is already registered.");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to identity service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
