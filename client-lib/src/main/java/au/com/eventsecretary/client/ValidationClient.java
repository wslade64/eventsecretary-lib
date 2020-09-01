package au.com.eventsecretary.client;

import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.registration.validation.ValidationRequest;
import au.com.eventsecretary.accounting.registration.validation.ValidationResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.Collections;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class ValidationClient extends AbstractClient {

    private final String systemToken;

    public ValidationClient(String systemToken, String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
        this.systemToken = systemToken;
    }

    public ValidationResponse validate(ValidationRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(systemToken));

            HttpEntity<ValidationRequest> httpEntity = new HttpEntity<>(request, headers);
            ResponseEntity<ValidationResponse> exchange = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, ValidationResponse.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("create:could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
