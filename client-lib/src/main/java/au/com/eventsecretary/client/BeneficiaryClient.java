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

import java.util.Arrays;
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

    public List<Beneficiary> findBeneficiariesByPersonid(List<String> peopleIds, String contextId) {
        try {
            String url = baseUrl + URI;

            HttpEntity<List<String>> httpEntity = createSystemEntityBody(peopleIds);

            if (contextId == null) {
                contextId = "all";
            }
            url += "?contextId=" + contextId;
            ResponseEntity<Beneficiary[]> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Beneficiary[].class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return Arrays.asList(exchange.getBody());
            }
            throw new ResourceExistsException("Could not retrieve beneficiaries at this moment." + exchange.getStatusCode());
        }
        catch (RestClientException e) {
            logger.error("Could not connect to payment service:" + e.getMessage());
            throw new UnexpectedSystemException("Could not retrieve beneficiaries at this moment.");
        }
    }
}
