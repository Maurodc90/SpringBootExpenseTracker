package com.maurodelcore.expensetracker.security;

import com.maurodelcore.expensetracker.service.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs once per request before Spring's own login filter. It reads the JWT from
 * the {@code Authorization} header and, if the token is valid, records who the
 * user is in the {@link SecurityContextHolder} so the access rules can be applied.
 * It identifies the user but does not decide what the user may access.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;

    /**
     * Extracts and validates the token, then stores the authenticated user for the
     * current request. The request always continues down the filter chain, with or
     * without an authenticated user.
     *
     * @param request     the incoming request
     * @param response    the outgoing response
     * @param filterChain the rest of the filters, which must always be called
     * @throws ServletException if a filter in the chain fails
     * @throws IOException      if reading or writing the request fails
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) { // the user sent a token
            token = authHeader.substring(7); // remove the "Bearer " prefix from the token
            try {
                username = jwtUtils.extractUsername(token);
            } catch (Exception ignored) {
                // This exception is ignored because a bad authentication will be handled by spring
            }
        }

        // Only authenticate if the token gave a username and nobody is authenticated for this request yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(username);
            if (jwtUtils.validateToken(token, userDetails.getUsername())) {
                // Create the authentication object and set it in the SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);

    }
}