package au.com.eventsecretary.apps;

import au.com.eventsecretary.client.SessionService;
import au.com.eventsecretary.client.UnauthorizedException;
import au.com.eventsecretary.people.Person;
import au.com.eventsecretary.user.identity.Authorisation;
import au.com.eventsecretary.user.identity.Identity;
import au.com.eventsecretary.user.identity.Permissions;
import au.com.eventsecretary.user.identity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static au.com.eventsecretary.Request.AUTH_COOKIE;

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

    protected Person person() {
        Person person = sessionService.getPerson();
        if (person == null) {
            throw new UnauthorizedException();
        }
        return person;
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

    protected boolean isSystem() {
        Identity identity = sessionService.getIdentity();
        if (identity == null) {
            return false;
        }
        return identity.getRole() == Role.SYSTEM;
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

    public static boolean hasRole(Authorisation authorisation, String authorisationArea, Permissions permission) {
        return authorisation.getRoles().stream().filter(areaRole -> areaRole.getArea().equals(authorisationArea)
                && areaRole.getPermissions().contains(permission)).findFirst().isPresent();
    }

    protected void addCookie(HttpServletRequest request, HttpServletResponse httpResponse, String token) {
        addCookie(request, httpResponse, AUTH_COOKIE, token);
    }

    public static void addCookie(HttpServletRequest request, HttpServletResponse httpResponse, String cookieName, String token) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setPath("/");
        cookie.setDomain(request.getServerName());
        cookie.setSecure(request.isSecure());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        cookie.setVersion(1);
        httpResponse.addCookie(cookie);
    }

    protected void clearCookie(HttpServletRequest request, HttpServletResponse httpResponse) {
        clearCookie(request, httpResponse, AUTH_COOKIE);
    }

    public static void clearCookie(HttpServletRequest request, HttpServletResponse httpResponse, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath("/");
        cookie.setDomain(request.getServerName());
        cookie.setSecure(request.isSecure());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setVersion(1);
        httpResponse.addCookie(cookie);
    }

    public static void decorateResponse(HttpHeaders headers, String filename)
    {
        if (filename.endsWith("pdf")) {
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
        } else if (filename.endsWith("xlsx")) {
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        }
        String format = String.format("attachment; filename=%s", filename);
        headers.set("Content-Disposition", format);
    }
}
