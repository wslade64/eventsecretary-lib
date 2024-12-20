package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.account.Account;
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
public class AccountClient extends AbstractClient {
    private static final String URI = "/payment/accounts";

    public AccountClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public void reset() {
        try {
            HttpEntity<PersonIdentity> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/reset", HttpMethod.POST, httpEntity, Void.class);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                return;
            }
            switch (wrap(exchange.getStatusCode())) {
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

    public Account createAccount(Account account) {
        try {
            HttpEntity<Account> httpEntity = createSystemEntityBody(account);

            ResponseEntity<Account> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Account.class);
            switch (wrap(exchange.getStatusCode())) {
                case CREATED:
                    // List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("create:could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    /**
     * Update name, organisationId
     * @param account
     */
    public void updateAccount(Account account) {
        try {
            HttpEntity<Account> httpEntity = createSystemEntityBody(account);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.PUT, httpEntity, Void.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("create:could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void sealAccount(String accountCode) {
        try {
            HttpEntity<Account> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/" + accountCode + "/seal", HttpMethod.PUT, httpEntity, Void.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("seal:could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void unsealAccount(String accountCode) {
        try {
            HttpEntity<Account> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/" + accountCode + "/unseal", HttpMethod.PUT, httpEntity, Void.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("seal:could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Account fetchAccountByCode(String accountCode) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Account> exchange = restTemplate.exchange(baseUrl + URI + "/" + accountCode
                , HttpMethod.GET, httpEntity, Account.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException("account");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public List<String> verifyAccount(String accountCode) {

        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<List<String>> exchange = restTemplate.exchange(baseUrl + "/payment/v1/admin?accountCode=" + accountCode
                    , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<String>>(){});
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException("account");
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
