package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.equestrian.event.Event;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

/**
 * @author sladew
 */
public class EventClient extends AbstractClient {
    private static final String URI = "/equestrian-ms/v1/event";

    public EventClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public String organisationEventExport(String organisationId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Event> exchange = restTemplate.exchange(baseUrl + URI + "/export/organisation?organisationId=" + organisationId
                    , HttpMethod.GET, httpEntity, Event.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getHeaders().getLocation().toString();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Event fetchEventByCode(String eventCode) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Event> exchange = restTemplate.exchange(baseUrl + URI + "/" + eventCode + "?options=event"
                , HttpMethod.GET, httpEntity, Event.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException("event");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
