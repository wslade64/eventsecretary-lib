package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

import static au.com.eventsecretary.Request.*;

/**
 * @author sladew
 */
public abstract class AbstractClient {

    private static final String SYSTEM = "2882df7b-4743-466e-ad44-06e8eccc1f7d";
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final String baseUrl;
    protected final RestTemplate restTemplate;

    public AbstractClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        this.baseUrl = baseUrl;
        restTemplate = restTemplateBuilder.build();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                switch (clientHttpResponse.getStatusCode()) {
                    case PRECONDITION_FAILED:
                    case CONFLICT:
                        break;
                    case BAD_REQUEST:
                        throw new ResourceNotFoundException("Bad request");
                    case NOT_FOUND:
                        throw new ResourceNotFoundException("Not found");
                    case UNAUTHORIZED:
                        logger.info("login:" + clientHttpResponse.getStatusCode());
                        throw new UnauthorizedException();
                    default:
                        super.handleError(clientHttpResponse);
                }
            }
        });
    }

    protected static HttpHeaders headers(String bearer) {
        HttpHeaders headers = new HttpHeaders();
        if (bearer != null) {
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(bearer));
        }
        if (isSandbox()) {
            headers.put(SANDBOX, Collections.singletonList("true"));
        }
        return headers;
    }

    protected static <T> HttpEntity<T> createEntity() {
        return new HttpEntity<>(headers(bearer()));
    }

    protected static <T> HttpEntity<T> createSystemEntity() {
        return new HttpEntity<>(headers(SYSTEM));
    }

    protected static <T> HttpEntity<T> createEntityBody(T body) {
        return new HttpEntity<>(body, headers(bearer()));
    }

    protected static <T> HttpEntity<T> createSystemEntityBody(T body) {
        return new HttpEntity<>(body, headers(SYSTEM));
    }
}
