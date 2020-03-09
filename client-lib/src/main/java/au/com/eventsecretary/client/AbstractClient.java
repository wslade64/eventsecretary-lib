package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

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

    protected static <T> HttpEntity<T> createEntity() {
        HttpHeaders headers = new HttpHeaders();
        String bearer = MDC.get(SecurityInterceptor.BEARER_KEY);
        if (bearer != null) {
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(bearer));
        }
        return new HttpEntity<>(headers);
    }

    protected static <T> HttpEntity<T> createSystemEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(SYSTEM));
        return new HttpEntity<>(headers);
    }

    protected static <T> HttpEntity<T> createEntityBody(T body) {
        HttpHeaders headers = new HttpHeaders();
        String bearer = MDC.get(SecurityInterceptor.BEARER_KEY);
        if (bearer != null) {
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(bearer));
        }
        return new HttpEntity<>(body, headers);
    }

    protected static <T> HttpEntity<T> createSystemEntityBody(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(SYSTEM));
        return new HttpEntity<>(body, headers);
    }
}
