package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static au.com.eventsecretary.client.SessionService.bearer;
import static au.com.eventsecretary.client.SessionService.isSandbox;
import static org.springframework.http.HttpStatus.*;

/**
 * @author sladew
 */
public abstract class AbstractClient {
    static final String LOCAL_HEADER = "x-system";

    @Value("${systemToken}")
    private String SYSTEM;

    @Value("${localToken}")
    private String LOCAL;


    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final String baseUrl;
    protected final RestTemplate restTemplate;

    public AbstractClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        this.baseUrl = baseUrl;
        restTemplate = restTemplateBuilder.build();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                HttpStatusCode statusCode = clientHttpResponse.getStatusCode();
                if (statusCode.value() == PRECONDITION_FAILED.value() || statusCode.value() == CONFLICT.value()) {
                    return;
                } else if (statusCode.value() == UNAUTHORIZED.value()) {
                    logger.info("login:" + clientHttpResponse.getStatusCode());
                    throw new UnauthorizedException();
                } else if (statusCode.value() == HttpStatus.BAD_REQUEST.value()) {
                    throw new ResourceNotFoundException("Bad request");
                } else if (statusCode.value() == NOT_FOUND.value()) {
                    throw new ResourceNotFoundException("Not found");
                } else {
                    super.handleError(clientHttpResponse);
                }
//                switch (statusCode.value()) {
//                    case PRECONDITION_FAILED:
//                    case CONFLICT:
//                        break;
//                    case BAD_REQUEST:
//                        throw new ResourceNotFoundException("Bad request");
//                    case NOT_FOUND:
//                        throw new ResourceNotFoundException("Not found");
//                    case UNAUTHORIZED:
//                        logger.info("login:" + clientHttpResponse.getStatusCode());
//                        throw new UnauthorizedException();
//                    default:
//                        super.handleError(clientHttpResponse);
//                }
            }
        });
    }

    protected HttpHeaders headers(String bearer) {
        HttpHeaders headers = new HttpHeaders();
        if (bearer != null) {
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(bearer));
        }
        if (isSandbox()) {
            headers.put(SecurityInterceptor.SANDBOX, Collections.singletonList("true"));
        }
        headers.put(LOCAL_HEADER, Collections.singletonList(LOCAL));
        return headers;
    }

    protected static Map<String, String> params(String... values) {
        HashMap<String, String> params = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            if (values[i+1] != null) {
                params.put(values[i], values[i+1]);
            }
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

    protected ObjectMapper objectMapper() {
        for (HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                return ((MappingJackson2HttpMessageConverter)messageConverter).getObjectMapper();
            }
        }
        throw new UnexpectedSystemException("Could not find object mapper");
    }

    public static HttpStatus wrap(HttpStatusCode statusCode) {
        int value = statusCode.value();
        if (value == OK.value()) {
            return HttpStatus.OK;
        }
        if (value == CREATED.value()) {
            return HttpStatus.CREATED;
        }
        if (value == NOT_FOUND.value()) {
            return HttpStatus.NOT_FOUND;
        }
        if (value == CONFLICT.value()) {
            return HttpStatus.CONFLICT;
        }
        if (value == HttpStatus.UNAUTHORIZED.value()) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (value == HttpStatus.UNAUTHORIZED.value()) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (value == HttpStatus.PRECONDITION_FAILED.value()) {
            return HttpStatus.PRECONDITION_FAILED;
        }
        if (value == NON_AUTHORITATIVE_INFORMATION.value()) {
            return NON_AUTHORITATIVE_INFORMATION;
        }
        if (value == SERVICE_UNAVAILABLE.value()) {
            return SERVICE_UNAVAILABLE;
        }
        if (value == CREATED.value()) {
            return CREATED;
        }
        throw new UnexpectedSystemException("Unexpected system status: " + statusCode);
    }
}
