package com.threeit.users.config;


import com.threeit.users.entities.User;
import com.threeit.users.repositories.UserRepository;
import com.threeit.users.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;


    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Filters incoming HTTP requests, checking for a Bearer token in the Authorization header.
     * If a valid Bearer token is found, the token is authenticated.
     *
     * @param request the HttpServletRequest object, providing request information for HTTP servlets
     * @param response the HttpServletResponse object, assisting a servlet in sending a response to the client
     * @param filterChain the FilterChain object, allowing the filter to pass on the request and response to the next entity in the chain
     * @throws ServletException if an exception occurs that interferes with the filter's operation
     * @throws IOException if an I/O error occurs during the handling of the request
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        if (!jwtTokenUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid token");
            return;
        }

        authenticateToken(token);

        filterChain.doFilter(request, response);
    }

    /**
     * Authenticates a JWT token by validating its signature and verifying the token's
     * association with a user in the system. If valid, sets the Spring Security context
     * with the authenticated user's details.
     *
     * @param token the JWT token to be authenticated
     */
    private void authenticateToken(String token) {
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            Optional<User> foundUser = userRepository.findByEmail(email);

            if (foundUser.isPresent() && token.equals(foundUser.get().getToken())) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(email)
                        .password("")
                        .authorities("USER")
                        .build();

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                );
            }
        }
    }
}
