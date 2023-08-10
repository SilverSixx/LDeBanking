package com.silversixx.bankingapp.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silversixx.bankingapp.utils.JwtProperties;
import com.silversixx.bankingapp.entity.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@Slf4j
@RequiredArgsConstructor
public class LoginProcessingFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    // Manage the authentication that is to verify the credentials and create tokens
    private final JwtProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info(username);
        log.info(password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            return authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException ex) {
            log.info("An exception thrown during authentication process: "+ ex.getMessage());
            handleAuthenticationException(request, response, ex);
            return null;
        }
    }
    private void handleAuthenticationException(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Map<Class<? extends AuthenticationException>, String> exceptionMessages = new HashMap<>();
        exceptionMessages.put(BadCredentialsException.class, "Invalid username or password");
        exceptionMessages.put(UsernameNotFoundException.class, "Username not found");
        exceptionMessages.put(AccountExpiredException.class, "Your account has expired");
        exceptionMessages.put(DisabledException.class, "Your account is disabled");
        exceptionMessages.put(LockedException.class, "Your account is locked");
        exceptionMessages.put(AuthenticationServiceException.class, "Some authentication service error");
        exceptionMessages.put(CredentialsExpiredException.class, "Your credentials have expired");
        String errorMessage = exceptionMessages.getOrDefault(ex.getClass(), "Authentication failed");
        try {
            response.getWriter().write("{\"message\":\"" + errorMessage + "\"}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserPrincipal user = (UserPrincipal) authResult.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256(properties.getSecretKey().getBytes());
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                .withClaim("authorities",
                        user.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList())
                )
                .withClaim("password", user.getPassword())
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withIssuer(request.getRequestURI())
                .sign(algorithm);
        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", accessToken);
        tokenResponse.put("refresh_token", refreshToken);
        String jsonResponse = objectMapper.writeValueAsString(tokenResponse);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonResponse);
        log.info(jsonResponse);
    }
}
