package au.com.eventsecretary.client;

import au.com.eventsecretary.UnexpectedSystemException;
import au.com.eventsecretary.people.Person;
import au.com.eventsecretary.user.identity.Authorisation;
import au.com.eventsecretary.user.identity.Identity;
import au.com.eventsecretary.user.identity.Permissions;
import au.com.eventsecretary.user.identity.Role;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.List;

import static au.com.eventsecretary.Request.*;

public class SessionService {
    private final AuthenticateClient authenticateClient;
    private final ThreadLocal<Session> cache = new ThreadLocal<>();

    private class Session {
        private Person person;
        private List<Authorisation> authorisations;
        private Identity identity;

        private Session(String token) {
            if (StringUtils.hasLength(token)) {
                try {
                    MDC.put(BEARER_KEY, token);
                    identity = authenticateClient.findIdentity();
                    if (identity != null) {
                        if (identity.getPersonId() != null) {
                            MDC.put(PERSON_KEY, identity.getPersonId());
                        }
                        MDC.put(IDENTITY_KEY, identity.getId());
                    }
                } catch (Exception e) {
                    MDC.remove(BEARER_KEY);
                }
            }
        }

        private void end() {
            MDC.remove(IDENTITY_KEY);
            MDC.remove(BEARER_KEY);
            MDC.remove(SANDBOX_KEY);
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
                return person = authenticateClient.findPerson();
            }
            return null;
        }

        private List<Authorisation> getAuthorisations() {
            if (identity == null) {
                throw new UnauthorizedException();
            }

            if (authorisations == null) {
                authorisations = authenticateClient.findAuthorisations();
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

            if (!authorisations.stream().anyMatch(authorisation -> authorisation.getContextName().equals(contextName)
                    && authorisation.getTargetId().equals(targetId)
                    && authorisation.getRoles().stream().anyMatch(areaRole
                    -> areaRole.getArea().equals(area) && areaRole.getPermissions().contains(read)))) {
                throw new UnauthorizedException();
            }
        }
    }

    public SessionService(AuthenticateClient authenticateClient) {
        this.authenticateClient = authenticateClient;
    }

    void begin(String token) {
        if (cache.get() != null) {
            new UnexpectedSystemException("cache present");
        }
        cache.set(new Session(token));
    }

    void end() {
        session().end();
        cache.remove();
    }

    public Identity getIdentity() {
        return session().getIdentity();
    }

    public Person getPerson() {
        return session().getPerson();
    }

    public void authorise(String contextName, String targetId, String area, Permissions read) {
        session().authorise(contextName, targetId, area, read);
    }

    public List<Authorisation> getAuthorisations() {
        return session().getAuthorisations();
    }

    private Session session() {
        Session session = cache.get();
        if (session == null) {
            new UnexpectedSystemException("cache not present");
        }
        return session;
    }
}
