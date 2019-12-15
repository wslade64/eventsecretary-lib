package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.user.identity.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author sladew
 */
@Component
public class IdentityClient {
    private static final String URI = "/user/v1/identity";

    Logger logger = LoggerFactory.getLogger(IdentityClient.class);
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public IdentityClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        this.baseUrl = baseUrl;
        restTemplate = restTemplateBuilder.build();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                switch (clientHttpResponse.getStatusCode()) {
                    case CONFLICT:
                        logger.info("login:" + clientHttpResponse.getStatusCode());
                        throw new ResourceExistsException("The email address is already in use");
                    case UNAUTHORIZED:
                        logger.info("login:" + clientHttpResponse.getStatusCode());
                        throw new UnauthorizedException();
                    default:
                        super.handleError(clientHttpResponse);
                }
            }
        });
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
