package au.com.eventsecretary.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static au.com.eventsecretary.Request.AUTH_COOKIE;

/**
 * @author sladew
 */
public class SecurityInterceptor implements HandlerInterceptor {
    static final String IP_KEY = "RealIp";
    final static String SANDBOX = "sandbox";

    Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

    @Autowired
    private SessionService sessionService;

    public static String realIP() {
        return MDC.get(IP_KEY);
    }

    public boolean preHandle(HttpServletRequest request
            , HttpServletResponse response
            , Object handler) throws Exception {

        MDC.put(IP_KEY, request.getHeader("X-Real-IP"));

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

        String sandbox = request.getHeader(SANDBOX);

        try {
            sessionService.begin(token, "true".equals(sandbox));
        } catch (UnauthorizedException e) {
            sessionService.begin(null, "true".equals(sandbox));
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
                if (cookie.getName().equals("JSESSIONID")) {
                    return StringUtils.isEmpty(cookie.getValue()) ? null : cookie.getValue();
                }
            }
        }
        return null;
    }

    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
//        sessionService.end();
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                         @Nullable Exception ex) throws Exception {
        MDC.remove(IP_KEY);
        sessionService.end();
    }
}