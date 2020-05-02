package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.common.Identifiable;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * @author sladew
 */
public class ResourceClient<T extends Identifiable> extends AbstractClient {
    Class<T> targetClass;

    public ResourceClient(String baseUrl, RestTemplateBuilder restTemplateBuilder, Class<T> targetClass) {
        super(baseUrl, restTemplateBuilder);
        this.targetClass = targetClass;
    }

    public List<T> getResources() {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<List<T>> exchange = restTemplate.exchange(baseUrl, HttpMethod.GET, httpEntity, ParameterizedTypeReference.forType(targetClass));
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getResource:" + e.getMessage());
            throw new UnexpectedSystemException("getResource");
        }
    }

    public T createResource(T resource) {
        try {
            logger.info("create:" + resource.getName());

            HttpEntity<T> httpEntity = createEntityBody(resource);

            ResponseEntity<T> exchange = restTemplate.exchange(baseUrl, HttpMethod.POST, httpEntity, targetClass);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    return exchange.getBody();
                case CONFLICT:
                    throw new ResourceExistsException("Resource exists.");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("createResource:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public T updateResource(T resource) {
        try {
            logger.info("update:" + resource.getName());

            HttpEntity<T> httpEntity = createEntityBody(resource);

            ResponseEntity<T> exchange = restTemplate.exchange(baseUrl, HttpMethod.PUT, httpEntity, targetClass);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("createResource:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
