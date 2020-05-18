package au.com.eventsecretary.apps;

import au.com.eventsecretary.client.UnauthorizedException;
import au.com.eventsecretary.user.identity.Identity;
import au.com.eventsecretary.user.identity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public abstract class AbstractController
{
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @ModelAttribute("identity")
    public Identity getIdentity(HttpServletRequest request) {
        return (Identity) request.getAttribute(Identity.class.getName());
    }

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

    protected Identity system(Identity identity) {
        if (identity == null) {
            throw new UnauthorizedException();
        }

        if (identity.getRole() == Role.SYSTEM) {
            return identity;
        }

        throw new UnauthorizedException();
    }

    protected void authenticated(Identity identity) {
        if (identity == null) {
            throw new UnauthorizedException();
        }
    }
    protected boolean isSystem(Identity identity) {
        authenticated(identity);
        return identity.getRole() == Role.SYSTEM;
    }

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
