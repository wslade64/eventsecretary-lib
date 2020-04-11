package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.membership.Membership;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sladew
 */
public class MembershipClient extends AbstractClient {
    private static final String URI = "/payment/membership";

    public MembershipClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public List<Membership> getMembershipsByPersonId(String personId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("personId", personId);
            ResponseEntity<List<Membership>> exchange = restTemplate.exchange(baseUrl + URI + "?personId={personId}"
                    , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Membership>>(){}, uriVariables);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getMembership:" + e.getMessage());
            throw new UnexpectedSystemException("getMembership");
        }
    }

    public Membership getMembership(String membershipRef) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Membership> exchange = restTemplate.exchange(baseUrl + URI + "/" + membershipRef, HttpMethod.GET, httpEntity, Membership.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getMembership:" + e.getMessage());
            throw new UnexpectedSystemException("getMembership");
        }
    }

    public String createMembership(Membership membership) {
        try {
            logger.info("create:" + membership.getName());

            HttpEntity<Membership> httpEntity = createSystemEntityBody(membership);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return locationHeader.get(0);
                case CONFLICT:
                    throw new ResourceExistsException("Membership already exists.");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("createMembership:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void updateMembership(Membership Membership) {
        try {
            logger.info("update:" + Membership.getName());

            HttpEntity<Membership> httpEntity = createSystemEntityBody(Membership);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/" + Membership.getId(), HttpMethod.PUT, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                case CONFLICT:
                    throw new ResourceExistsException("Membership already exists.");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("updateMembership:" + e.getMessage());
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
