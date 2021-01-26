package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.account.Order;
import au.com.eventsecretary.accounting.registration.Registration;
import au.com.eventsecretary.common.note.Note;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author sladew
 */
public class NoteClient extends AbstractClient {
    private static final String URI = "/notification/v1/note";

    public NoteClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public Note createNote(Note note) {
        try {
            HttpEntity<Note> httpEntity = createSystemEntityBody(note);

            ResponseEntity<Note> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Note.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("create:clientError:{}", e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Note updateNote(Note note) {
        try {
            HttpEntity<Note> httpEntity = createSystemEntityBody(note);

            ResponseEntity<Note> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.PUT, httpEntity, Note.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("update:clientError:{}", e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }


    public Order fetchOrderByCode(String accountCode, String orderCode) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Order> exchange = restTemplate.exchange(baseUrl + URI + "/{orderCode}"
                    , HttpMethod.GET, httpEntity, Order.class, params("accountCode", accountCode, "orderCode", orderCode));
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException("order");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public List<Note> queryNotes(Note note) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + URI);
            if (isNotBlank(note.getContainerContext())) {
                uriComponentsBuilder.queryParam("containerContext", note.getContainerContext());
            }
            if (isNotBlank(note.getContainerId())) {
                uriComponentsBuilder.queryParam("containerId", note.getContainerId());
            }
            if (isNotBlank(note.getTargetContext())) {
                uriComponentsBuilder.queryParam("targetContext", note.getTargetContext());
            }
            if (isNotBlank(note.getTargetId())) {
                uriComponentsBuilder.queryParam("targetId", note.getTargetId());
            }
            if (isNotBlank(note.getPersonId())) {
                uriComponentsBuilder.queryParam("personId", note.getPersonId());
            }
            if (note.getStatus() != null) {
                uriComponentsBuilder.queryParam("status", note.getStatus());
            }
            if (note.getPurpose() != null) {
                uriComponentsBuilder.queryParam("purpose", note.getPurpose());
            }
            if (note.getSeverity() != null) {
                uriComponentsBuilder.queryParam("severity", note.getSeverity());
            }

            ResponseEntity<List<Note>> exchange = restTemplate.exchange(uriComponentsBuilder.toUriString()
                    , HttpMethod.GET, httpEntity
                    , new ParameterizedTypeReference<List<Note>>(){}
                    );
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void deleteNote(String noteId) {
        try {
            logger.info("delete:{}", noteId);

            HttpEntity<Registration> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/{noteId}", HttpMethod.DELETE, httpEntity, Void.class, params("orderId", noteId));
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("delete:clientError:{}", e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
