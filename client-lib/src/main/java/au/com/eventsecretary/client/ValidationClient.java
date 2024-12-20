package au.com.eventsecretary.client;

import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.registration.validation.ValidationRequest;
import au.com.eventsecretary.accounting.registration.validation.ValidationResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class ValidationClient extends AbstractClient {

    private final String systemToken;

    private static class ErrorResponse {
        List<ValidationError> errors;
        public List<ValidationError> getErrors() {
            return errors;
        }
    }

    public ValidationClient(String systemToken, String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
        this.systemToken = systemToken;
    }

    public ValidationResponse validate(ValidationRequest request) {
        try {
            HttpHeaders headers = headers(systemToken);

            HttpEntity<ValidationRequest> httpEntity = new HttpEntity<>(request, headers);
            ResponseEntity<String> exchange = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, String.class);
            String body = exchange.getBody();
            ObjectMapper objectMapper = objectMapper();
            try {
                switch (wrap(exchange.getStatusCode())) {
                    case CREATED:
                    case OK:
                        return objectMapper.readValue(body, ValidationResponse.class);
                    case PRECONDITION_FAILED:
                        throw new ValidationErrorException(objectMapper.readValue(body, ErrorResponse.class).getErrors());
                    default:
                        throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
                }
            } catch (JsonParseException e) {
                throw new UnexpectedSystemException(e);

            } catch (JsonMappingException e) {
                throw new UnexpectedSystemException(e);
            } catch (IOException e) {
                throw new UnexpectedSystemException(e);
            }
        }
        catch (RestClientException e) {
            logger.error("create:could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
