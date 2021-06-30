package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

/**
 * @author sladew
 */
public class PaymentClient extends AbstractClient
{
    private static final String URI = "/payment/v1";

    public PaymentClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    static class CheckoutRequest {
        private String system;
        private String orderId;
        private String amount;
        private String nonce;

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        public String getSystem() {
            return system;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getAmount() {
            return amount;
        }

        public String getNonce() {
            return nonce;
        }
    }

    private static class Refund {
        private String transactionId;
        private String amount;

        public String getTransactionId()
        {
            return transactionId;
        }

        public void setTransactionId(String transactionId)
        {
            this.transactionId = transactionId;
        }

        public String getAmount()
        {
            return amount;
        }

        public void setAmount(String amount)
        {
            this.amount = amount;
        }
    }

    public String refund(String bearer, String paymentId, String amount) {
        try {
            String url = baseUrl + URI + "/refund";
            logger.info("refund:" + bearer);

            Refund refund = new Refund();
            refund.setAmount(amount);
            refund.setTransactionId(paymentId);
            HttpEntity<Refund> httpEntity = createEntityBody(refund);

            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                case PRECONDITION_FAILED:
                    throw new ResourceExistsException(exchange.getBody());
            }
            throw new ResourceExistsException("Could not perform refund at this moment." + exchange.getStatusCode());
        }
        catch (RestClientException e) {
            logger.error("Could not connect to payment service:" + e.getMessage());
            throw new UnexpectedSystemException("Could not perform refund at this moment.");
        }
    }

    public void voucher(String amount, String orderName) {
        try {
            String url = baseUrl + URI + "/checkout2";

            CheckoutRequest checkoutRequest = new CheckoutRequest();
            checkoutRequest.setAmount(amount);
            checkoutRequest.setNonce("voucher");
            checkoutRequest.setSystem("order");
            checkoutRequest.setOrderId(orderName);

            HttpEntity<CheckoutRequest> httpEntity = createSystemEntityBody(checkoutRequest);

            ResponseEntity<Void> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                case PRECONDITION_FAILED:
                    throw new ResourceExistsException("");
            }
            throw new ResourceExistsException("Could not checkout at this moment." + exchange.getStatusCode());
        }
        catch (RestClientException e) {
            logger.error("Could not connect to payment service:" + e.getMessage());
            throw new UnexpectedSystemException("Could not perform checkout at this moment.");
        }
    }
}
