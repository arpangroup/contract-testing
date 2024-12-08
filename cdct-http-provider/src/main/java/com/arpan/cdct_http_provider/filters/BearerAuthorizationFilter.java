package com.arpan.cdct_http_provider.filters;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;

@Component
@Slf4j
public class BearerAuthorizationFilter extends OncePerRequestFilter {
    public static final long ONE_HOUR = 60 * 60 * 1000L;
    private static final String REGEX_BEARER_TOKEN = "Bearer [a-zA-Z0-9+/=]+";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (tokenValid(header)) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("user", null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            log.error("Bearer Token Not Valid: {}", header);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            // Write JSON response
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            response.getWriter().flush();
        }
    }

    /*private boolean tokenValid(String header) {
        boolean hasBearerToken = StringUtils.isNotEmpty(header) && header.startsWith("Bearer ");
        if (hasBearerToken) {
            String token = header.substring("Bearer ".length());
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.put(Base64.getDecoder().decode(token));
            buffer.flip();
            long timestamp = buffer.getLong();
            return System.currentTimeMillis() - timestamp <= ONE_HOUR;
        } else {
            return false;
        }
    }*/

    private boolean tokenValid(String header) {
        boolean hasBearerToken = StringUtils.isNotEmpty(header) && header.startsWith("Bearer ");
        return hasBearerToken;
    }

   /* private boolean tokenValid(String header) {
        boolean hasBearerToken = StringUtils.isNotEmpty(header) && header.startsWith("Bearer ");
        if (header.matches(REGEX_BEARER_TOKEN)) {
            return true; // Token does not match the regex
        }
        return false;
    }*/

}
