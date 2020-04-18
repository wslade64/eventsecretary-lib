package au.com.eventsecretary.client;

import au.com.eventsecretary.user.identity.Identity;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static au.com.eventsecretary.Request.*;

/**
 * @author sladew
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private IdentityClient identityClient;

    @Override
    public boolean preHandle(HttpServletRequest request
            , HttpServletResponse response
            , Object handler) throws Exception {

        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.isEmpty(bearer)) {
            bearer = request.getParameter("bearer");
        }
        if (StringUtils.hasLength(bearer)) {
            Identity identity = fetchIdentity(bearer);
            if (identity != null) {
                request.setAttribute(Identity.class.getName(), identity);
                MDC.put(IDENTITY_KEY, identity.getEmail());
                MDC.put(BEARER_KEY, bearer);
            }
        }

        String sandbox = request.getHeader(SANDBOX);
        if ("true".equals(sandbox)) {
            MDC.put(SANDBOX_KEY, sandbox);
        }
        return true;
    }

    protected Identity fetchIdentity(String bearer) {
        return identityClient.get(bearer);
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        MDC.remove(IDENTITY_KEY);
        MDC.remove(BEARER_KEY);
        MDC.remove(SANDBOX_KEY);
    }
}