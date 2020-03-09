package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.organisation.Association;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * @author sladew
 */
@Component
public class AssociationClient extends AbstractClient {
    private static final String URI = "/payment/association";

    public AssociationClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public Association getAssociationByName(String name) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Association> exchange = restTemplate.exchange(baseUrl + URI + "?name=" + name, HttpMethod.GET, httpEntity, Association.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getAssociation:" + e.getMessage());
            throw new UnexpectedSystemException("getAssociation");
        }
    }

    public Association getAssociationByCode(String code) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Association> exchange = restTemplate.exchange(baseUrl + URI + "?code=" + code, HttpMethod.GET, httpEntity, Association.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getAssociation:" + e.getMessage());
            throw new UnexpectedSystemException("getAssociation");
        }
    }

    public Association getAssociation(String associationRef) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Association> exchange = restTemplate.exchange(baseUrl + URI + "/" + associationRef, HttpMethod.GET, httpEntity, Association.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getAssociation:" + e.getMessage());
            throw new UnexpectedSystemException("getAssociation");
        }
    }

    public List<Association> getAssociations() {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<List<Association>> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Association>>(){});
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getAssociations:" + e.getMessage());
            throw new UnexpectedSystemException("getAssociations");
        }
    }

    public String createAssociation(Association association) {
        try {
            logger.info("create:" + association.getName());

            HttpEntity<Association> httpEntity = createSystemEntityBody(association);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return locationHeader.get(0);
                case CONFLICT:
                    throw new ResourceExistsException("Association already exists.");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("createAssociation:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void reset() {
        try {
            logger.info("reset");

            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/reset", HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("reset:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
