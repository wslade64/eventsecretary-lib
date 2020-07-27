package au.com.eventsecretary.client;

import net.sf.javaprinciples.notification.Notification;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;

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

}
