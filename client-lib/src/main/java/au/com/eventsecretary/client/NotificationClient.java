package au.com.eventsecretary.client;

import au.com.eventsecretary.UnexpectedSystemException;
import net.sf.javaprinciples.notification.Notification;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;

import static au.com.eventsecretary.client.SessionService.bearer;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class NotificationClient extends AbstractClient {
    private static final String URI = "/notification";

    public NotificationClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public void send(Notification notification, Resource file) {
        HttpHeaders requestHeaders = headers(bearer());
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        if (file != null) {
            HttpHeaders requestHeadersMulti = new HttpHeaders();
            requestHeadersMulti.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("notification", new HttpEntity<>(notification, requestHeaders));
            parts.add("attachment", file);
            HttpEntity requestEntity = new HttpEntity(parts, requestHeadersMulti);
            restTemplate.exchange(baseUrl + URI + "/v1/notificationAttachment", HttpMethod.POST, requestEntity, Void.class);
        } else {
            HttpEntity requestEntity = new HttpEntity(notification, requestHeaders);
            restTemplate.exchange(baseUrl + URI + "/v1/notification", HttpMethod.POST, requestEntity, Void.class);
        }
    }

    public String uploadDocument(Resource resource) {
        HttpHeaders requestHeaders = headers(bearer());
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("document", resource);
        HttpEntity requestEntity = new HttpEntity(parts, requestHeaders);
        ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/v1/document", HttpMethod.POST, requestEntity, Void.class);
        switch (exchange.getStatusCode()) {
            case CREATED:
                List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                return locationHeader.get(0);
            default:
                throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
        }
    }
}
