package com.example.demo.filter;

import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer";

    private final UserService userService;

    public JwtRequestFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return super.shouldNotFilter(request) || request.getServletPath().startsWith("/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = getTokenFromRequest(request);
        var username = JwtUtil.extractUsername(token);

        if (username != null && usernameDoesNotHasToken()) {
            var userDetails = userService.loadUserByUsername(username);
            if (JwtUtil.validateToken(token, userDetails)) {
                handleAuthenticationWhenTokenValid(request, userDetails);
            }
        }

        chain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        var authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new RuntimeException("No jwt token in the request");
        }

        return authorizationHeader.replaceFirst(BEARER_PREFIX, "");
    }

    private boolean usernameDoesNotHasToken() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void handleAuthenticationWhenTokenValid(HttpServletRequest request, UserDetails userDetails) {
        var usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

}
