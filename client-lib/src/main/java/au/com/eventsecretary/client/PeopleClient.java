package au.com.eventsecretary.client;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.people.Address;
import au.com.eventsecretary.people.ContactDetails;
import au.com.eventsecretary.people.Person;
import au.com.eventsecretary.people.presentation.PersonIdentity;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;

import static au.com.eventsecretary.simm.IdentityUtils.cleanEmailAddress;
import static au.com.eventsecretary.simm.IdentityUtils.cleanPhoneNumber;

/**
 * @author sladew
 */
public class PeopleClient extends AbstractClient {
    private static final String URI = "/user/v1/people";

    public static boolean hasAddress(Person person) {
        Address mailingAddress = person.getMailingAddress();
        return mailingAddress != null && StringUtils.hasLength(mailingAddress.getPostCode());
    }

    public static void copyContactDetails(Person from, Person to) {
        if (to.getContactDetails() == null) {
            to.setContactDetails(from.getContactDetails());
        } else {
            ContactDetails fromContactDetails = from.getContactDetails();
            ContactDetails toContactDetails = to.getContactDetails();
            if (fromContactDetails != null) {
                if (org.apache.commons.lang3.StringUtils.isBlank(toContactDetails.getEmailAddress())) {
                    toContactDetails.setEmailAddress(fromContactDetails.getEmailAddress());
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(toContactDetails.getPhoneNumber())) {
                    toContactDetails.setPhoneNumber(fromContactDetails.getPhoneNumber());
                }
            }
        }
    }


    public static void assignName(Person person) {
        if (person == null) {
            return;
        }
        person.setName(personName(person));
    }

    public static String personName(Person person) {
        if (person == null) {
            return null;
        }
        if (person.getFirstName() == null) {
            return person.getLastName();
        }
        if (person.getLastName() == null) {
            return person.getFirstName();
        }
        return String.format("%s %s", person.getFirstName(), person.getLastName());
    }

    public static void cleanPerson(Person person) {
        ContactDetails contactDetails = person.getContactDetails();
        if (contactDetails != null) {
            contactDetails.setPhoneNumber(cleanPhoneNumber(contactDetails.getPhoneNumber()));
            contactDetails.setEmailAddress(cleanEmailAddress(contactDetails.getEmailAddress()));
        }

        person.setFirstName(org.apache.commons.lang3.StringUtils.trim(person.getFirstName()));
        person.setLastName(org.apache.commons.lang3.StringUtils.trim(person.getLastName()));
        assignName(person);
    }

    public PeopleClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public Person getPerson(String personId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Person> exchange = restTemplate.exchange(baseUrl + URI + "/person/" + personId, HttpMethod.GET, httpEntity, Person.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to person service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Person findPerson() {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Person[]> exchange = restTemplate.exchange(baseUrl + URI + "/person", HttpMethod.GET, httpEntity, Person[].class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody()[0];
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to person service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Person findPersonByIdentityId(String identityId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Person> exchange = restTemplate.exchange(baseUrl + URI + "/identity/" + identityId, HttpMethod.GET, httpEntity, Person.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("findPersonByIdentityId:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public PersonIdentity findPersonIdentityByPersonId(String personId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<PersonIdentity> exchange = restTemplate.exchange(baseUrl + URI + "/personIdentity/" + personId, HttpMethod.GET, httpEntity, PersonIdentity.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public PersonIdentity findPersonIdentityByIdentityId(String identityId) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<PersonIdentity> exchange = restTemplate.exchange(baseUrl + URI + "/personIdentity?identityId=" + identityId, HttpMethod.GET, httpEntity, PersonIdentity.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void reset() {
        try {
            HttpEntity<PersonIdentity> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/reset", HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
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

    public PersonIdentity createPersonIdentity(PersonIdentity personIdentity) {
        try {
            HttpEntity<PersonIdentity> httpEntity = createSystemEntityBody(personIdentity);

            ResponseEntity<PersonIdentity> exchange = restTemplate.exchange(baseUrl + URI + "/personIdentity", HttpMethod.POST, httpEntity, PersonIdentity.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    // List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return exchange.getBody();
                case CONFLICT:
                    throw new ResourceExistsException("email");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("get:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public PersonIdentity updatePersonIdentity(PersonIdentity personIdentity) {
        try {
            HttpEntity<PersonIdentity> httpEntity = createSystemEntityBody(personIdentity);

            ResponseEntity<PersonIdentity> exchange = restTemplate.exchange(baseUrl + URI + "/personIdentity", HttpMethod.PUT, httpEntity, PersonIdentity.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody();
                case CONFLICT:
                    throw new ResourceExistsException("email");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("updatePersonIdentity:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public String createPerson(Person person) {
        try {
            HttpEntity<Person> httpEntity = createSystemEntityBody(person);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/person", HttpMethod.POST, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case CREATED:
                    List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return locationHeader.get(0);
                case CONFLICT:
                    throw new ResourceExistsException("email");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("createPerson:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void updatePerson(Person person) {
        try {
            HttpEntity<Person> httpEntity = createSystemEntityBody(person);

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/person", HttpMethod.PUT, httpEntity, Void.class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return;
                case CONFLICT:
                    throw new ResourceExistsException("email");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("updatePerson:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public void deletePerson(String personId) {
        try {
            logger.info("delete:" + personId);

            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Void> exchange = restTemplate.exchange(baseUrl + URI + "/person/" + personId, HttpMethod.DELETE, httpEntity, Void.class);
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

    public List<Person> getPersonsByNames(String firstName, String lastName) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Person[]> exchange = restTemplate.exchange(baseUrl + URI + "/person?" + "firstName=" + firstName + "&lastName=" + lastName, HttpMethod.GET, httpEntity, Person[].class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return Arrays.asList(exchange.getBody());
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("getPersonByNames:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Person getPersonByDetails(String firstName, String lastName, String emailAddress) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Person[]> exchange = restTemplate.exchange(baseUrl + URI + "/person?"
                    + "firstName=" + firstName
                    + "&lastName=" + lastName
                    + "&emailAddress=" + emailAddress, HttpMethod.GET, httpEntity, Person[].class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return exchange.getBody().length == 0 ? null : exchange.getBody()[0];
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("getPersonByNames:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public List<Person> findPeople(String query) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Person[]> exchange = restTemplate.exchange(baseUrl + URI + "/person?" + "query=" + query, HttpMethod.GET, httpEntity, Person[].class);
            switch (exchange.getStatusCode()) {
                case OK:
                    return Arrays.asList(exchange.getBody());
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + exchange.getStatusCode());
            }
        }
        catch (RestClientException e) {
            logger.error("findPeople:could not connect to person service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
