package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.organisation.Association;
import au.com.eventsecretary.equestrian.event.Event;
import au.com.eventsecretary.leaderboard.Leaderboard;
import au.com.eventsecretary.leaderboard.LeaderboardEvent;
import au.com.eventsecretary.leaderboard.result.EventResult;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * @author sladew
 */
public class LeaderboardClient extends AbstractClient {
    private static final String URI = "/leaderboard-ms/v1/";

    public LeaderboardClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public String createLeaderboardEvent(String leaderboardCode, LeaderboardEvent leaderboardEvent) {
        try {
            HttpEntity<LeaderboardEvent> httpEntity = createSystemEntityBody(leaderboardEvent);

            ResponseEntity<Event> exchange = restTemplate.exchange(baseUrl + URI + "/" + leaderboardCode + "/event"
                    , HttpMethod.POST, httpEntity, Event.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody().getId();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        } catch (RestClientException e) {
            logger.error("could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void uploadLeaderboardResults(String leaderboardCode, String leaderboardEventCode, List<EventResult> eventResults) {
        try {
            HttpEntity<List<EventResult>> httpEntity = createSystemEntityBody(eventResults);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/" + leaderboardCode + "/event/" + leaderboardEventCode + "/results"
                    , HttpMethod.POST, httpEntity, Void.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        } catch (RestClientException e) {
            logger.error("could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}