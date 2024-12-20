package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.account.Order;
import au.com.eventsecretary.accounting.pricing.Cost;
import au.com.eventsecretary.accounting.pricing.CostAmount;
import au.com.eventsecretary.accounting.registration.Registration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static au.com.eventsecretary.NumberUtility.add;
import static au.com.eventsecretary.simm.NumberUtils.formatCurrency;

/**
 * @author sladew
 */
public class OrderClient extends AbstractClient {
    private static final String URI = "/payment/accounts/{accountCode}/order";

    public OrderClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public static Cost findCostByTarget(Cost cost, String context, String id) {
        if (StringUtils.equals(cost.getTargetContext(), context) && StringUtils.equals(cost.getTargetId(), id)) {
            return cost;
        }
        for (Cost childCost : cost.getCosts()) {
            Cost foundCost = findCostByTarget(childCost, context, id);
            if (foundCost != null) {
                return foundCost;
            }
        }
        return null;
    }

    public static List<Cost> findCostsByTarget(Cost cost, String context, String id) {
        List<Cost> targets = new ArrayList<>();
        for (Cost childCost : cost.getCosts()) {
            Cost foundCost = findCostByTarget(childCost, context, id);
            if (foundCost != null) {
                targets.add(foundCost);
            }
        }
        return targets;
    }

    public static List<Cost> findCostsByTarget(Cost cost, String context) {
        // Not recursive
        List<Cost> targets = new ArrayList<>();
        for (Cost childCost : cost.getCosts()) {
            if (StringUtils.equals(childCost.getTargetContext(), context)) {
                targets.add(childCost);
            }
        }
        return targets;
    }

    public Order createOrder(String accountCode, Order order) {
        try {
            HttpEntity<Order> httpEntity = createSystemEntityBody(order);

            ResponseEntity<Order> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Order.class, params("accountCode",accountCode));
            switch (wrap(exchange.getStatusCode())) {
                case CREATED:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException("order");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException("order");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public List<Order> fetchOrdersByCurrentPerson(String accountCode) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<List<Order>> exchange = restTemplate.exchange(baseUrl + URI
                    , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Order>>(){}, params("accountCode", accountCode));
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                case NOT_FOUND:
                    throw new ResourceNotFoundException("order");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("could not connect to service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void deleteOrder(String accountCode, String orderId) {
        try {
            logger.info("delete:{}", orderId);

            HttpEntity<Registration> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/{orderId}", HttpMethod.DELETE, httpEntity, Void.class, params("accountCode", accountCode, "orderId", orderId));
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("delete:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public static BigDecimal sumAmount(Cost cost) {
        BigDecimal total = null;
        for (CostAmount costAmount : cost.getAmount()) {
            total = add(total, costAmount.getAmount());
        }
        if (total == null || BigDecimal.ZERO.compareTo(total) == 0) {
            return null;
        }
        return total;
    }

    public static List<String[]> flattenOrder(Order order) {
        List<String[]> list = flattenCost(order.getCost());
        if (order.getLedger() != null) {
            String[] total;
            list.add(total = new String[2]);
            total[0] = "Total";
            total[1] = formatCurrency(order.getLedger().getTotal());
        }
        return list;
    }

    public static List<String[]> flattenCost(Cost cost) {
        List<String[]> list = new ArrayList<>();
        if (cost == null) {
            return list;
        }
        flattenCost(cost, list);
        return list;
    }

    private static void flattenCost(Cost cost, List<String[]> list) {
        for (Cost childCost : cost.getCosts()) {
            if (!childCost.getAmount().isEmpty()) {
                BigDecimal total = sumAmount(childCost);
                if (total != null) {
                    String[] pair;
                    list.add(pair = new String[2]);
                    pair[0] = StringUtils.isNotBlank(cost.getName()) ? String.format("%s - %s", cost.getName(), childCost.getName()) : childCost.getName();
                    pair[1] = formatCurrency(total);
                }
            }
        }
        for (Cost childCost : cost.getCosts()) {
            if (childCost.getAmount().isEmpty()) {
                flattenCost(childCost, list);
            }
        }
    }
}
