package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static au.com.eventsecretary.Request.*;

/**
 * @author sladew
 */
public abstract class AbstractClient {

    @Value("${systemToken}")
    private String SYSTEM;

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

    protected static Map<String, String> params(String... values) {
        HashMap<String, String> params = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            params.put(values[i], values[i+1]);
            i++;
        }
        return params;
    }

    protected static String queryParams(String[] keyValueList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < keyValueList.length; i += 2) {
            stringBuilder.append(i == 0 ? "?" : "&");
            stringBuilder.append(keyValueList[i]);
            stringBuilder.append("=");
            stringBuilder.append("{");
            stringBuilder.append(keyValueList[i]);
            stringBuilder.append("}");
        }
        return stringBuilder.toString();
    }

    protected <T> HttpEntity<T> createEntity() {
        return new HttpEntity<>(headers(bearer()));
    }

    protected <T> HttpEntity<T> createSystemEntity() {
        return new HttpEntity<>(headers(SYSTEM));
    }

    protected <T> HttpEntity<T> createEntityBody(T body) {
        return new HttpEntity<>(body, headers(bearer()));
    }

    protected <T> HttpEntity<T> createSystemEntityBody(T body) {
        return new HttpEntity<>(body, headers(SYSTEM));
    }
}
