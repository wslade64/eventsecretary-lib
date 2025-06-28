package au.com.eventsecretary.client;

import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.user.identity.Authorisation;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class AuthorisationClient extends ResourceClient {
    public AuthorisationClient(String baseUrl, RestTemplateBuilder restTemplateBuilder, Class targetClass) {
        super(baseUrl, restTemplateBuilder, targetClass);
    }

    public Authorisation getAuthorisationByContext(String identityId, String contentName, String targetId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("identityId", identityId);
            uriVariables.put("contextName", contentName);
            uriVariables.put("targetId", targetId);
            ResponseEntity<Authorisation> exchange = restTemplate.exchange(baseUrl + "?identityId={identityId}&contextName={contextName}&contextId={targetId}"
                    , HttpMethod.GET, httpEntity, targetClass, uriVariables);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getAuthorisation:" + e.getMessage());
            throw new UnexpectedSystemException("getAuthorisation");
        }
    }

    public List<Authorisation> getAuthorisationsByContext(String contextName, String targetId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("contextName", contextName);
            uriVariables.put("targetId", targetId);
            ResponseEntity<List<Authorisation>> exchange = restTemplate.exchange(baseUrl + "?contextName={contextName}&contextId={targetId}"
                    , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Authorisation>>(){}, uriVariables);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getAuthorisationsByContext:" + e.getMessage());
            throw new UnexpectedSystemException("getAuthorisationsByContext");
        }
    }

    public List<Authorisation> getAuthorisations() {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<List<Authorisation>> exchange = restTemplate.exchange(baseUrl
                    , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Authorisation>>(){});
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getAuthorisation:" + e.getMessage());
            throw new UnexpectedSystemException("getAuthorisation");
        }
    }

    public List<Authorisation> getAuthorisationsByIdentityId(String identityId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("identityId", identityId);
            ResponseEntity<List<Authorisation>> exchange = restTemplate.exchange(baseUrl + "?identityId={identityId}"
                    , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Authorisation>>(){}, uriVariables);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getAuthorisation:" + e.getMessage());
            throw new UnexpectedSystemException("getAuthorisation");
        }
    }
}
