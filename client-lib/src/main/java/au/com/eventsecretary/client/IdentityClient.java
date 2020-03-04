package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.user.identity.Identity;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

/**
 * @author sladew
 */
@Component
public class IdentityClient extends AbstractClient {
    private static final String URI = "/user/v1/identity";

    public IdentityClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public Identity get(String bearer) {
        try {
            logger.info("get:" + bearer);
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(bearer));
            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

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

    public String create(Identity userIdentity) {
        try {
            logger.info("create:" + userIdentity.getEmail());

            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList("820013bb-5655-42c4-8784-af94a82e668b"));
            HttpEntity<Identity> httpEntity = new HttpEntity(userIdentity, headers);

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
