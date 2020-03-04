package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.Collections;

/**
 * @author sladew
 */
public class PaymentClient extends AbstractClient
{
    private static final String URI = "/payment/v1";

    public PaymentClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
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
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(bearer));
            HttpEntity<Refund> httpEntity = new HttpEntity<>(refund, headers);

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

}
