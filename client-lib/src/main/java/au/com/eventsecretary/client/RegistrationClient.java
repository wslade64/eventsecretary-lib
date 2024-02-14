package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.registration.Registration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sladew
 */
public class RegistrationClient extends AbstractClient {
    private static final String URI = "/payment/registration";

    public RegistrationClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public List<Registration> getRegistrationsByOwnerId(String ownerId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("ownerId", ownerId);
            ResponseEntity<List<Registration>> exchange = restTemplate.exchange(baseUrl + URI + "?ownerId={ownerId}"
                    , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Registration>>(){}, uriVariables);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getRegistration:" + e.getMessage());
            throw new UnexpectedSystemException("getRegistration");
        }
    }

    public List<Registration> getRegistrationsByOwnersId(List<String> ownersId) {
        List<Registration> list = new ArrayList<>();
        boolean remaining = !ownersId.isEmpty();
        int index = 0;
        while (remaining) {
            int size = Math.min(ownersId.size() - index, 100);
            if (size == 0) {
                remaining = false;
            } else {
                list.addAll(getRegistrationsByOwnersIdBulk(ownersId.subList(index, index + size)));
                index += size;
            }
        }
        return list;

    }

    private List<Registration> getRegistrationsByOwnersIdBulk(List<String> ownersId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("ownersId", String.join(",", ownersId));
            ResponseEntity<List<Registration>> exchange = restTemplate.exchange(baseUrl + URI + "?ownersId={ownersId}"
                    , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Registration>>(){}, uriVariables);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getRegistration:" + e.getMessage());
            throw new UnexpectedSystemException("getRegistration");
        }
    }

    public Registration getRegistration(String registrationRef) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Registration> exchange = restTemplate.exchange(baseUrl + URI + "/" + registrationRef, HttpMethod.GET, httpEntity, Registration.class);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getRegistration:" + e.getMessage());
            throw new UnexpectedSystemException("getRegistration");
        }
    }

    public String createRegistration(Registration registration) {
        try {
            logger.info("create:" + registration.getName());

            HttpEntity<Registration> httpEntity = createSystemEntityBody(registration);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return locationHeader.get(0);
                case CONFLICT:
                    throw new ResourceExistsException("Registration already exists.");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("createRegistration:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void updateRegistration(Registration registration) {
        try {
            logger.info("update:" + registration.getName());

            HttpEntity<Registration> httpEntity = createSystemEntityBody(registration);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/" + registration.getId(), HttpMethod.PUT, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                case CONFLICT:
                    throw new ResourceExistsException("Registration already exists.");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("updateRegistration:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void deleteRegistrations(String ownerId) {
        try {
            logger.info("delete:" + ownerId);

            HttpEntity<Registration> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/owner/" + ownerId, HttpMethod.DELETE, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("delete:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void deleteRegistration(String registrationId) {
        try {
            logger.info("delete:" + registrationId);

            HttpEntity<Registration> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/" + registrationId, HttpMethod.DELETE, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("delete:" + e.getMessage());
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
