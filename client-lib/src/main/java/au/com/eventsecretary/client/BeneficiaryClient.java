package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.accounting.organisation.Association;
import au.com.eventsecretary.accounting.transfer.Beneficiary;
import au.com.eventsecretary.people.BankingDetails;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sladew
 */
public class BeneficiaryClient extends AbstractClient {
    private static final String URI = "/payment/v1/beneficiary";

    public BeneficiaryClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public Beneficiary createBeneficiary(Beneficiary beneficiary, BankingDetails bankingDetails) {
        try {
            Map<String, Object> values = new HashMap<>();
            values.put("beneficiary", beneficiary);
            values.put("bankingDetails", bankingDetails);
            HttpEntity<Map<String, Object>> httpEntity = createSystemEntityBody(values);

            ResponseEntity<Beneficiary> exchange = restTemplate.exchange(baseUrl + URI, HttpMethod.POST, httpEntity, Beneficiary.class);
            switch (wrap(exchange.getStatusCode())) {
                case CREATED:
                case OK:
                    return beneficiary;
                case CONFLICT:
                case PRECONDITION_FAILED:
                    throw new ResourceExistsException("exists");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("createBeneficiary:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void updateAssociation(Association association) {
        try {
            logger.info("update:" + association.getName());

            HttpEntity<Association> httpEntity = createSystemEntityBody(association);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/" + association.getId(), HttpMethod.PUT, httpEntity, Void.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return;
                case CONFLICT:
                case PRECONDITION_FAILED:
                    throw new ResourceExistsException("Code exists");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("updateAssociation:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void reset() {
        try {
            logger.info("reset");

            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/reset", HttpMethod.POST, httpEntity, Void.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("reset:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void deleteAssociation(String associationId) {
        try {
            logger.info("delete:" + associationId);

            HttpEntity<Void> httpEntity = createSystemEntity();
            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/" + associationId, HttpMethod.DELETE, httpEntity, Void.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return ;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("deleteResource:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

}
