package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.common.Identifiable;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author sladew
 */
public class ResourceClient<T extends Identifiable> extends AbstractClient {
    Class<T> targetClass;
    ParameterizedTypeReference<List<T>> parameterizedTypeReference;

    public ResourceClient(String baseUrl, RestTemplateBuilder restTemplateBuilder, Class<T> targetClass) {
        super(baseUrl, restTemplateBuilder);
        this.targetClass = targetClass;

        this.parameterizedTypeReference = new ParameterizedTypeReference<List<T>>() {
            @Override
            public Type getType() {
                Type type = super.getType();
                if (type instanceof ParameterizedType) {
                    return TypeUtils.parameterize(List.class, targetClass);
                }
                return type;
            }
        };
    }

    public T getResourceById(String id) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<T> exchange = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.GET, httpEntity, targetClass);
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

    public List<T> getResources() {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<List<T>> exchange = restTemplate.exchange(baseUrl, HttpMethod.GET, httpEntity, parameterizedTypeReference);
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

    public void deleteResource(String resourceId) {
        try {
            logger.info("delete:" + resourceId);

            HttpEntity<T> httpEntity = createEntity();
            ResponseEntity<T> exchange = restTemplate.exchange(baseUrl + "/" + resourceId, HttpMethod.DELETE, httpEntity, targetClass);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("deleteResource:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
