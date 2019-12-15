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

/**
 * @author sladew
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    public static final String IDENTITY_KEY = "Identity";
    public static final String BEARER_KEY = "Bearer";

    @Autowired
    private IdentityClient identityService;

    @Override
    public boolean preHandle(HttpServletRequest request
            , HttpServletResponse response
            , Object handler) throws Exception {

        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.isEmpty(bearer)) {
            bearer = request.getParameter("bearer");
        }
        if (StringUtils.hasLength(bearer)) {
            Identity identity = identityService.get(bearer);
            request.setAttribute(Identity.class.getName(), identity);
            MDC.put(IDENTITY_KEY, identity.getEmail());
            MDC.put(BEARER_KEY, identity.getBearer());
        }
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        MDC.remove(IDENTITY_KEY);
        MDC.remove(BEARER_KEY);
    }
}