package com.bodev.auth_api.config;

import com.bodev.auth_api.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;

    public RateLimiterInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Obtener IP del cliente
        String clientIp = request.getRemoteAddr();

        // Para login, usar email si está disponible
        String rateLimitKey = clientIp;
        if (request.getRequestURI().contains("/login")) {
            // Podríamos usar el email del body, pero es más simple usar IP
            rateLimitKey = "login:" + clientIp;
        }

        if (!rateLimiterService.tryConsume(rateLimitKey)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Demasiadas solicitudes. Intenta nuevamente en " +
                    rateLimiterService.getAvailableTokens(rateLimitKey) + " segundos.");
            return false;
        }

        return true;
    }
}