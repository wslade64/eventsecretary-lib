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
import org.springframework.web.client.RestClientException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static au.com.eventsecretary.simm.IdentityUtils.cleanEmailAddress;
import static au.com.eventsecretary.simm.IdentityUtils.cleanPhoneNumber;

/**
 * @author sladew
 */
public class PeopleClient extends AbstractClient {
    private static final String URI = "/user/v1/people";

    public static final String OPTION_EXTENSION = "extension";
    public static final String OPTION_ADDRESS = "address";
    public static final String OPTION_FINANCIAL = "financial";

    public static boolean hasAddress(Person person) {
        Address mailingAddress = person.getMailingAddress();
        return mailingAddress != null && StringUtils.isNotBlank(mailingAddress.getPostCode());
    }

    public static void copyContactDetails(Person from, Person to) {
        if (to.getContactDetails() == null) {
            to.setContactDetails(from.getContactDetails());
        } else {
            ContactDetails fromContactDetails = from.getContactDetails();
            ContactDetails toContactDetails = to.getContactDetails();
            if (fromContactDetails != null) {
                if (StringUtils.isBlank(toContactDetails.getEmailAddress())) {
                    toContactDetails.setEmailAddress(fromContactDetails.getEmailAddress());
                }
                if (StringUtils.isBlank(toContactDetails.getPhoneNumber())) {
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

    public static String personLastFirst(Person person) {
        if (person == null) {
            return "";
        }
        if (person.getFirstName() == null) {
            return person.getLastName();
        }
        if (person.getLastName() == null) {
            return person.getFirstName();
        }
        return String.format("%s, %s", person.getLastName(), person.getFirstName());
    }

    public static void cleanPerson(Person person) {
        ContactDetails contactDetails = person.getContactDetails();
        if (contactDetails != null) {
            contactDetails.setPhoneNumber(cleanPhoneNumber(contactDetails.getPhoneNumber()));
            contactDetails.setEmailAddress(cleanEmailAddress(contactDetails.getEmailAddress()));
        }

        person.setFirstName(StringUtils.trim(person.getFirstName()));
        person.setLastName(StringUtils.trim(person.getLastName()));
        assignName(person);
    }

    public PeopleClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public Person getPerson(String personId, String... options) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            String param = "";
            if (options.length > 0) {
                param = "?options=" + org.apache.commons.lang3.StringUtils.join(options, ",");
            }
            ResponseEntity<Person> exchange = restTemplate.exchange(baseUrl + URI + "/person/" + personId + param, HttpMethod.GET, httpEntity, Person.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody()[0];
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("findPersonByIdentityId:" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public PersonIdentity findPersonIdentityByPersonId(String personId, String[] ...options) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<PersonIdentity> exchange = restTemplate.exchange(baseUrl + URI + "/personIdentity/" + personId, HttpMethod.GET, httpEntity, PersonIdentity.class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return;
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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
            switch (wrap(exchange.getStatusCode())) {
                case CREATED:
                    // List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return exchange.getBody();
                case CONFLICT:
                case PRECONDITION_FAILED:
                    throw new ResourceExistsException("email");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody();
                case CONFLICT:
                case PRECONDITION_FAILED:
                    throw new ResourceExistsException("email");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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
            switch (wrap(exchange.getStatusCode())) {
                case CREATED:
                    List<String> locationHeader = exchange.getHeaders().get(HttpHeaders.LOCATION);
                    return locationHeader.get(0);
                case CONFLICT:
                case PRECONDITION_FAILED:
                    throw new ResourceExistsException("email");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return;
                case CONFLICT:
                case PRECONDITION_FAILED:
                    throw new ResourceExistsException("email");
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
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

    public List<Person> getPersonsByNames(String firstName, String lastName) {
        try {
            HttpEntity<Void> httpEntity = createEntity();

            ResponseEntity<Person[]> exchange = restTemplate.exchange(baseUrl + URI + "/person?" + "firstName=" + firstName + "&lastName=" + lastName, HttpMethod.GET, httpEntity, Person[].class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return Arrays.asList(exchange.getBody());
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("getPersonByNames:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public Person getPersonByDetails(String firstName, String lastName, String emailAddress) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Person[]> exchange = restTemplate.exchange(baseUrl + URI + "/person?"
                    + "firstName=" + firstName
                    + "&lastName=" + lastName
                    + "&emailAddress=" + emailAddress, HttpMethod.GET, httpEntity, Person[].class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return exchange.getBody().length == 0 ? null : exchange.getBody()[0];
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("getPersonByNames:could not connect to people service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public List<Person> findPeopleById(List<String> idList) {
        List<Person> list = new ArrayList<>();
        boolean remaining = !idList.isEmpty();
        int index = 0;
        while (remaining) {
            int size = Math.min(idList.size() - index, 50);
            if (size == 0) {
                remaining = false;
            } else {
                list.addAll(findPeopleByBlock(idList.subList(index, index + size)));
                index += size;
            }
        }
        return list;
    }

    public List<Person> findPeopleByBlock(List<String> idList, String... options) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            String ids = idList.stream().collect(Collectors.joining(","));

            if (options.length > 0) {
                ids += "&options=" + org.apache.commons.lang3.StringUtils.join(options, ",");
            }
            ResponseEntity<Person[]> exchange = restTemplate.exchange(baseUrl + URI + "/person?" + "ids=" + ids, HttpMethod.GET, httpEntity, Person[].class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return Arrays.asList(exchange.getBody());
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("findPeople:could not connect to person service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }

    public List<Person> findPeople(String query) {
        try {
            HttpEntity<Void> httpEntity = createSystemEntity();

            ResponseEntity<Person[]> exchange = restTemplate.exchange(baseUrl + URI + "/person?" + "query=" + query, HttpMethod.GET, httpEntity, Person[].class);
            switch (wrap(exchange.getStatusCode())) {
                case OK:
                    return Arrays.asList(exchange.getBody());
                default:
                    throw new UnexpectedSystemException("Invalid response code:" + wrap(exchange.getStatusCode()));
            }
        }
        catch (RestClientException e) {
            logger.error("findPeople:could not connect to person service" + e.getMessage());
            throw new UnexpectedSystemException(e);
        }
    }
}
