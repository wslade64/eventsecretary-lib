package au.com.eventsecretary.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static au.com.eventsecretary.Request.*;

/**
 * @author sladew
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

    @Autowired
    private SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request
            , HttpServletResponse response
            , Object handler) throws Exception {

        String token = extractTokenFromCookie(request);
        if (StringUtils.isEmpty(token)) {
            token = request.getHeader(HttpHeaders.AUTHORIZATION);
        }

        // This will be deprecated -> Only here for url downloads
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter("bearer");
        }

        if (StringUtils.isEmpty(token)) {
            token = null;
        }

        sessionService.begin(token);

        String sandbox = request.getHeader(SANDBOX);
        if ("true".equals(sandbox)) {
            MDC.put(SANDBOX_KEY, sandbox);
        }
        return true;
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTH_COOKIE)) {
                    return StringUtils.isEmpty(cookie.getValue()) ? null : cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        MDC.remove(SANDBOX_KEY);
        sessionService.end();
    }
}