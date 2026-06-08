package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.facility.booking.BookingRequest;
import au.com.eventsecretary.facility.facility.Facility;
import au.com.eventsecretary.facility.facility.FacilityCategory;
import au.com.eventsecretary.facility.facility.FacilityType;
import au.com.eventsecretary.facility.site.Site;
import au.com.eventsecretary.people.Address;
import au.com.eventsecretary.people.presentation.PersonIdentity;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * @author sladew
 */
public class MapClient extends AbstractClient {
    private static final String URI = "/facility/v1";

    public MapClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public String geocode(Address address) {
        try {
            HttpEntity<Address> httpEntity = createSystemEntityBody(address);

            ResponseEntity<String> exchange = restTemplate.exchange(baseUrl + URI + "/map/geocode", HttpMethod.POST, httpEntity, String.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("create:could not connect to maps service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
