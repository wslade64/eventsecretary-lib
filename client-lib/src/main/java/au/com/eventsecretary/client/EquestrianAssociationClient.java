package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.organisation.Association;
import au.com.eventsecretary.equestrian.organisation.EquestrianAssociation;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * @author sladew
 */
public class EquestrianAssociationClient extends AbstractClient {
    private static final String URI = "/equestrian-ms/v1/association";

    public EquestrianAssociationClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public EquestrianAssociation getAssociationByCode(String name) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<EquestrianAssociation> exchange = restTemplate.exchange(baseUrl + URI + "/" + name, HttpMethod.GET, httpEntity, EquestrianAssociation.class);
            if (wrap(exchange.getStatusCode()) == HttpStatus.OK) {
                return exchange.getBody();
            }
            return null;
        }
        catch (RestClientException e) {
            logger.error("getAssociation:" + e.getMessage());
            throw new UnexpectedSystemException("getAssociation");
        }
    }
}
