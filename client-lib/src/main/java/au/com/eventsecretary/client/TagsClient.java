package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.vendor.Structure;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

/**
 * @author sladew
 */
public class TagsClient extends AbstractClient {
    private static final String URI = "/equestrian-ms/v1/tags";

    public TagsClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public Structure createStructure(Structure structure) {
        try {
            HttpEntity<Structure> httpEntity = createSystemEntityBody(structure);

            ResponseEntity<Structure> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Structure.class);
            switch (wrap(exchange.getStatusCode())) {
                case CREATED:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("create:could not connect to site service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Structure fetchStructureByName(String tagName) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Structure> exchange = restTemplate.exchange(baseUrl + URI + "/{name}"
                    , HttpMethod.GET, httpEntity, Structure.class, params("name", tagName));
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException(tagName);
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
