package au.com.eventsecretary.client;

import au.com.eventsecretary.people.Person;
import au.com.eventsecretary.user.identity.Authorisation;
import au.com.eventsecretary.user.identity.Identity;

import java.util.List;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface AuthenticateClient {
    Identity findIdentity();
    Person findPerson();
    List<Authorisation> findAuthorisations();
}
