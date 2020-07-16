package au.com.eventsecretary.client;

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
    private static final String AUTH_COOKIE = "es-auth";

    @Autowired
    private SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request
            , HttpServletResponse response
            , Object handler) throws Exception {

        String token = extractTokenFromCookie(request);
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.isEmpty(bearer)) {
            bearer = request.getParameter("bearer");
        }
        sessionService.begin(bearer);

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
                if (cookie.getSecure() && cookie.isHttpOnly() && cookie.getName().equals(AUTH_COOKIE)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        addCookie(request, response);
        MDC.remove(SANDBOX_KEY);
        sessionService.end();
    }

    private void addCookie(HttpServletRequest request, HttpServletResponse httpResponse) {
        String header = httpResponse.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasLength(header)) {
            return;
        }
//        httpResponse.setHeader(HttpHeaders.AUTHORIZATION, null);
        Cookie cookie = new Cookie(AUTH_COOKIE, header);
        cookie.setPath("/");
        cookie.setDomain(request.getServerName());
        httpResponse.addHeader("Access-Control-Allow-Origin", request.getServerName());
        cookie.setSecure(request.isSecure());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        httpResponse.addCookie(cookie);
    }
}