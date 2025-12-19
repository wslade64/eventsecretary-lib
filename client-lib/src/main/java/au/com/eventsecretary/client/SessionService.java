package au.com.eventsecretary.client;

import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.people.Person;
import au.com.eventsecretary.people.PersonImpl;
import au.com.eventsecretary.user.identity.Authorisation;
import au.com.eventsecretary.user.identity.Identity;
import au.com.eventsecretary.user.identity.IdentityImpl;
import au.com.eventsecretary.user.identity.Permissions;
import au.com.eventsecretary.user.identity.Role;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.List;

public class SessionService {
    private static final String IDENTITY_KEY = "Identity";
    private static final String BEARER_KEY = "Bearer";
    private static final String SANDBOX_KEY = "Sandbox";

    private final AuthenticateClient authenticateClient;
    private final ThreadLocal<Session> cache = new ThreadLocal<>();

    @Value("${systemToken}")
    String systemToken;

    private class Session {
        private Person person;
        private Person effectivePerson;
        private List<Authorisation> authorisations;
        private Identity identity;
        private boolean sandbox;

        private Session(String token) {
            if (StringUtils.hasLength(token)) {
                MDC.put(BEARER_KEY, token);
                if (StringUtils.pathEquals(systemToken, token)) {
                    identity = new IdentityImpl();
                    identity.setName("System");
                    identity.setEmail("system");
                    identity.setRole(Role.SYSTEM);
                    person = new PersonImpl();
                    person.setName("System");
                } else {
                    identity = authenticateClient.findIdentity(token);
                }
                if (identity != null) {
                    MDC.put(IDENTITY_KEY, identity.getEmail());
                }
            }
        }

        private void end() {
            MDC.remove(IDENTITY_KEY);
            MDC.remove(BEARER_KEY);
        }

        private Identity getIdentity() {
            return identity;
        }

        private Person getPerson() {
            if (identity == null) {
                return null;
            }
            if (person != null) {
                return person;
            }
            if (identity.getPersonId() != null) {
                return person = authenticateClient.findPerson(identity.getPersonId());
            }
            return null;
        }

        private Person getEffectivePerson() {
            if (effectivePerson != null) {
                return effectivePerson;
            }
            return getPerson();
        }

        private List<Authorisation> getAuthorisations() {
            if (identity == null) {
                throw new UnauthorizedException();
            }

            if (authorisations == null) {
                authorisations = findAuthorisations(identity.getId());
            }
            return authorisations;

        }

        private void authorise(String contextName, String targetId, String area, Permissions read) {
            if (identity == null) {
                throw new UnauthorizedException();
            }

            if (identity.getRole() == Role.SYSTEM) {
                return;
            }

            List<Authorisation> authorisations = getAuthorisations();
            hasPermission(authorisations, contextName, targetId, area, read);
        }
    }

    public SessionService(AuthenticateClient authenticateClient) {
        this.authenticateClient = authenticateClient;
    }

    public void begin(String token, boolean sandbox) {
        if (cache.get() != null) {
            throw new UnexpectedSystemException("cache present");
        }
        if (sandbox) {
            MDC.put(SANDBOX_KEY, "sandbox");
        }
        Session session;
        cache.set(session = new Session(token));
        session.sandbox = sandbox;
    }

    public void begin(Object object) {
        if (!(object instanceof Session)) {
            throw new UnexpectedSystemException("Invalid session");
        }
        if (cache.get() != null) {
            throw new UnexpectedSystemException("cache present");
        }
        Session session = (Session) object;
        if (session.sandbox) {
            MDC.put(SANDBOX_KEY, "sandbox");
        }
        cache.set(session);
    }

    public void end() {
        MDC.remove(SANDBOX_KEY);
        session().end();
        cache.remove();
    }


    public Identity getIdentity() {
        return session().getIdentity();
    }

    public Person getPerson() {
        return session().getPerson();
    }

    public Person getEffectivePerson() {
        return session().getEffectivePerson();
    }

    public void authorise(String contextName, String targetId, String area, Permissions read) {
        session().authorise(contextName, targetId, area, read);
    }

    public void effectivePerson(Person person) {
        session().effectivePerson = person;
    }
    public List<Authorisation> getAuthorisations() {
        return session().getAuthorisations();
    }

    public Object copySession() {
        return session();
    }

    private Session session() {
        Session session = cache.get();
        if (session == null) {
            new UnexpectedSystemException("cache not present");
        }
        return session;
    }

    public static void hasPermission(List<Authorisation> authorisations, String contextName, String targetId, String area, Permissions read) {
        if (!authorisations.stream().anyMatch(authorisation -> authorisation.getContextName().equals(contextName)
                && (targetId == null || authorisation.getTargetId().equals(targetId))
                && authorisation.getRoles().stream().anyMatch(areaRole
                -> areaRole.getArea().equals(area) && areaRole.getPermissions().contains(read)))) {
            throw new UnauthorizedException();
        }
    }
    public static boolean isSandbox() {
        return MDC.get(SANDBOX_KEY) != null;
    }

    public static String bearer() {
        return MDC.get(BEARER_KEY);
    }

    public List<Authorisation> findAuthorisations(String identityId) {
        return this.authenticateClient.findAuthorisations(identityId);
    }

}
