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
public class AuthenticateClientDelegate implements AuthenticateClient {
    private final IdentityClient identityClient;
    private final AuthorisationClient authorisationClient;
    private final PeopleClient peopleClient;

    AuthenticateClientDelegate(IdentityClient identityClient, AuthorisationClient authorisationClient, PeopleClient peopleClient) {
        this.identityClient = identityClient;
        this.authorisationClient = authorisationClient;
        this.peopleClient = peopleClient;
    }


    @Override
    public Identity findIdentity() {
        return identityClient.findIdentity();
    }

    @Override
    public Person findPerson() {
        return peopleClient.findPerson();
    }

    @Override
    public List<Authorisation> findAuthorisations() {
        return authorisationClient.getAuthorisations();
    }
}
