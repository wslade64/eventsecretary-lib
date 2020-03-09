package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.organisation.Organisation;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sladew
 */
@Component
public class OrganisationClient extends AbstractClient {
    private static final String URI = "/payment/organisation";

    public OrganisationClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public Organisation getOrganisationByName(String associationRef, String name) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("name", name);
            uriVariables.put("association", associationRef);
            ResponseEntity<Organisation> exchange = restTemplate.exchange(baseUrl + URI + "?name={name}&association={association}"
                    , HttpMethod.GET, httpEntity, Organisation.class, uriVariables);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getOrganisation:" + e.getMessage());
            throw new UnexpectedSystemException("getOrganisation");
        }
    }

    public Organisation getOrganisationByCode(String code) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Organisation> exchange = restTemplate.exchange(baseUrl + URI + "?code=" + code, HttpMethod.GET, httpEntity, Organisation.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getOrganisation:" + e.getMessage());
            throw new UnexpectedSystemException("getOrganisation");
        }
    }

    public Organisation getOrganisation(String organisationRef) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Organisation> exchange = restTemplate.exchange(baseUrl + URI + "/" + organisationRef, HttpMethod.GET, httpEntity, Organisation.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getOrganisation:" + e.getMessage());
            throw new UnexpectedSystemException("getOrganisation");
        }
    }

    public String createOrganisation(Organisation organisation) {
        try {
            logger.info("create:" + organisation.getName());

            HttpEntity<Organisation> httpEntity = createSystemEntityBody(organisation);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return locationHeader.get(0);
                case CONFLICT:
                    throw new ResourceExistsException("Organisation already exists.");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("createOrganisation:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void updateOrganisation(Organisation organisation) {
        try {
            logger.info("update:" + organisation.getName());

            HttpEntity<Organisation> httpEntity = createSystemEntityBody(organisation);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/" + organisation.getId(), HttpMethod.PUT, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                case CONFLICT:
                    throw new ResourceExistsException("Organisation already exists.");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("updateOrganisation:" + e.getMessage());
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
