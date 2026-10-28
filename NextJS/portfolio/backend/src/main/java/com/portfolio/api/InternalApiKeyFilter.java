package com.portfolio.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Only the internal Next.js proxy can submit contacts. The backend is not exposed publicly. */
@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {
    private final byte[] expected;

    public InternalApiKeyFilter(@Value("${app.internal-api-key}") String apiKey) {
        if (apiKey == null || apiKey.isBlank() || apiKey.length() < 32) {
            throw new IllegalStateException("INTERNAL_API_KEY must be at least 32 characters.");
        }
        this.expected = apiKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals("/api/v1/contact");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String supplied = request.getHeader("X-Internal-Api-Key");
        byte[] provided = supplied == null ? new byte[0] : supplied.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, provided)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Unauthorized.\"}");
            return;
        }
        chain.doFilter(request, response);
    }
}
