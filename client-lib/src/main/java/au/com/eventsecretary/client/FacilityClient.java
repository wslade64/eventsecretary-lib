package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.facility.booking.BookingRequest;
import au.com.eventsecretary.facility.facility.Facility;
import au.com.eventsecretary.facility.facility.FacilityCategory;
import au.com.eventsecretary.facility.facility.FacilityType;
import au.com.eventsecretary.facility.site.Site;
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
public class FacilityClient extends AbstractClient {
    private static final String URI = "/facility/v1";

    public FacilityClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public void reset() {
        try {
            HttpEntity<PersonIdentity> httpEntity = createEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/reset", HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("reset:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Site createSite(Site site) {
        try {
            HttpEntity<Site> httpEntity = createEntityBody(site);

            ResponseEntity<Site> exchange = restTemplate.exchange(baseUrl + URI + "/site", HttpMethod.POST, httpEntity, Site.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    // List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
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

    public Site fetchSite(String siteRef) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Site> exchange = restTemplate.exchange(baseUrl + URI + "/site/" + siteRef
                , HttpMethod.GET, httpEntity, Site.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException(siteRef);
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("fetchSite:could not connect to site service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Site findSiteByCode(String code) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Site> exchange = restTemplate.exchange(baseUrl + URI + "/site/" + code
                    , HttpMethod.GET, httpEntity, Site.class);
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

    public void deleteSite(String code) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/site/" + code
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

    public Facility createFacility(Site site, FacilityType facilityType, Facility facility) {
        try {
            HttpEntity<Facility> httpEntity = createEntityBody(facility);

            ResponseEntity<Facility> exchange = restTemplate.exchange(baseUrl + URI + "/site/" + site.getCode() + "/facility/" + facilityType.getCode()
                    , HttpMethod.POST, httpEntity, Facility.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("findFacility:could not connect to site service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public List<Facility> findFacilities(Site site, FacilityType facilityType) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<List<Facility>> exchange = restTemplate.exchange(baseUrl + URI + "/site/" + site.getCode() + "/facility/" + facilityType.getCode()
                    , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Facility>>() {
                    });
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("findFacility:could not connect to site service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public List<Facility> findFacilitiesByCategory(Site site, FacilityCategory facilityCategory) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<List<Facility>> exchange = restTemplate.exchange(baseUrl + URI + "/site/" + site.getCode() + "/facility/query?category=" + facilityCategory
                    , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Facility>>() {
                    });
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("findFacility:could not connect to site service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void createBooking(Site site, BookingRequest bookingRequest) {
        try {
            HttpEntity<BookingRequest> httpEntity = createEntityBody(bookingRequest);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/site/" + site.getCode() + "/booking"
                    , HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("createBooking:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
