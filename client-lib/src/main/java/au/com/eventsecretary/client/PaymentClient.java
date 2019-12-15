package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

/**
 * @author sladew
 */
@Component
public class PaymentClient
{
    private static final String URI = "/payment/v1";

    Logger logger = LoggerFactory.getLogger(PaymentClient.class);
    private final String baseUrl;
    private final RestTemplate restTemplate;

    public PaymentClient(@Value("${userUrl}") String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        this.baseUrl = baseUrl;
        restTemplate = restTemplateBuilder.build();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                switch (clientHttpResponse.getStatusCode()) {
                    case PRECONDITION_FAILED:
                        break;
                    case UNAUTHORIZED:
                        logger.info("login:" + clientHttpResponse.getStatusCode());
                        throw new UnauthorizedException();
                    default:
                        super.handleError(clientHttpResponse);
                }
            }
        });
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
