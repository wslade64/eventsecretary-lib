package au.com.eventsecretary.apps;

import au.com.eventsecretary.client.SessionService;
import au.com.eventsecretary.client.UnauthorizedException;
import au.com.eventsecretary.user.identity.Identity;
import au.com.eventsecretary.user.identity.Permissions;
import au.com.eventsecretary.user.identity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public abstract class AbstractController
{
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected SessionService sessionService;

    protected void authorise(String contextName, String targetId, String area, Permissions read) {
        sessionService.authorise(contextName, targetId, area, read);
    }

    @Deprecated
    protected void authorise(Identity userIdentity, String contextName, String targetId, String area, Permissions read) {
        sessionService.authorise(contextName, targetId, area, read);
    }

    @Deprecated
    protected Identity authorize(Identity identity, String siteCode) {
        if (identity == null) {
            throw new UnauthorizedException();
        }

        if (identity.getRole() == Role.SYSTEM) {
            return identity;
        }

        if (siteCode != null && identity.getRole() == Role.ADMIN && identity.getDomain() != null) {
            // The site must be included in the domain
            if (identity.getDomain().contains(siteCode)) {
                return identity;
            }
            logger.error("identity {} does not have {}", identity.getEmail(), siteCode);
        }
        throw new UnauthorizedException();
    }

    protected Identity system() {
        return system(sessionService.getIdentity());
    }

    protected Identity authenticated() {
        Identity identity = sessionService.getIdentity();
        if (identity == null) {
            throw new UnauthorizedException();
        }
        return identity;
    }

    protected Identity identity() {
        return sessionService.getIdentity();
    }

    @Deprecated
    protected Identity system(Identity identity) {
        if (identity == null) {
            throw new UnauthorizedException();
        }

        if (identity.getRole() == Role.SYSTEM) {
            return identity;
        }

        throw new UnauthorizedException();
    }

    @Deprecated
    protected void authenticated(Identity identity) {
        if (identity == null) {
            throw new UnauthorizedException();
        }
    }

    @Deprecated
    protected boolean isSystem(Identity identity) {
        authenticated(identity);
        return identity.getRole() == Role.SYSTEM;
    }

    @Deprecated
    protected boolean isUser(Identity identity) {
        authenticated(identity);
        return (identity.getRole() == null || identity.getRole() == Role.USER) && identity.getPersonId() != null;
    }

    @Deprecated
    protected boolean isAdmin(Identity identity, String siteCode) {
        if (identity == null) {
            return false;
        }
        if (identity.getRole() == Role.SYSTEM) {
            return true;
        }

        if (siteCode != null && identity.getRole() == Role.ADMIN && identity.getDomain() != null) {
            // The site must be included in the domain
            if (identity.getDomain().contains(siteCode)) {
                return true;
            }
        }
        return false;
    }
}
