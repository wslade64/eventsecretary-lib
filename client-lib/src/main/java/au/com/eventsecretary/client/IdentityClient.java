package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.user.identity.Identity;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
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

    public Identity get(String bearer) {
        try {
            HttpEntity<Void> httpEntity = new HttpEntity<>(headers(bearer));

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
            params.put("emailAddress", emailAddress);
            ResponseEntity<List<Identity>> exchange = restTemplate.exchange(baseUrl + URI + "?email={emailAddress}", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Identity>>() {}, params);
            switch (exchange.getStatusCode()) {
                case OK:
                    List<Identity> identityList = exchange.getBody();
                    if (identityList == null || identityList.isEmpty()) {
                        throw new ResourceNotFoundException(emailAddress);
                    }
                    if (identityList.size() > 1) {
                        throw new UnexpectedSystemException("Multiple identity for " + emailAddress);
                    }
                    return identityList.get(0);
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

    public String create(Identity userIdentity) {
        try {
            logger.info("create:" + userIdentity.getEmail());

            HttpEntity<Identity> httpEntity = createSystemEntityBody(userIdentity);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
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
