package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.account.Order;
import au.com.eventsecretary.accounting.registration.Registration;
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
public class OrderClient extends AbstractClient {
    private static final String URI = "/payment/accounts/{accountCode}/order";

    public OrderClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public Order createOrder(String accountCode, Order order) {
        try {
            HttpEntity<Order> httpEntity = createSystemEntityBody(order);

            ResponseEntity<Order> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Order.class, params("accountCode",accountCode));
            switch (exchange.getStatusCode()) {
                case CREATED:
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

    public Order updateOrder(String accountCode, Order order) {
        try {
            HttpEntity<Order> httpEntity = createSystemEntityBody(order);

            ResponseEntity<Order> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.PUT, httpEntity, Order.class, params("accountCode",accountCode));
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("updateOrder:{}" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }


    public Order fetchOrderByName(String orderName) {
        String[] split = orderName.split("-");
        if (split.length != 2) {
            throw new UnexpectedSystemException("Invalid order name");
        }
        return fetchOrderByCode(split[0], split[1]);
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

    public List<Order> fetchOrders(String accountCode) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<List<Order>> exchange = restTemplate.exchange(baseUrl + URI + "?filter=ACCOUNT"
                    , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Order>>(){}, params("accountCode", accountCode));
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

    public void deleteOrder(String orderId) {
        try {
            logger.info("delete:" + orderId);

            HttpEntity<Registration> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/" + orderId, HttpMethod.DELETE, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("delete:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
