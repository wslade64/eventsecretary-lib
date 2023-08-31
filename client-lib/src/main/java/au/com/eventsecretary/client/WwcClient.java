package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.equestrian.organisation.WorkingWithChildren;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

/**
 * @author sladew
 */
public class WwcClient extends AbstractClient {
    private static final String URI = "";

    public WwcClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public WorkingWithChildren createWwc(WorkingWithChildren wwc) {
        try {
            HttpEntity<WorkingWithChildren> httpEntity = createSystemEntityBody(wwc);

            ResponseEntity<WorkingWithChildren> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, WorkingWithChildren.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("create:could not connect to site service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void updateWwc(WorkingWithChildren wwc) {
        try {
            HttpEntity<WorkingWithChildren> httpEntity = createSystemEntityBody(wwc);

            ResponseEntity<WorkingWithChildren> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.PUT, httpEntity, WorkingWithChildren.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("create:could not connect to site service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public WorkingWithChildren findWwc(String id) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<WorkingWithChildren> exchange = restTemplate.exchange(baseUrl + URI + "/" + id
                , HttpMethod.GET, httpEntity, WorkingWithChildren.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException(id);
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("fetchSite:could not connect to site service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public WorkingWithChildren findWwcByPersonId(String personId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<WorkingWithChildren> exchange = restTemplate.exchange(baseUrl + URI + queryParams(new String[] {"personId", personId})
                    , HttpMethod.GET, httpEntity, WorkingWithChildren.class, params("personId", personId));
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("findSiteByCode:could not connect to site service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void deleteWwc(String id) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/" + id
                    , HttpMethod.DELETE, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("delete:could not connect to site service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
