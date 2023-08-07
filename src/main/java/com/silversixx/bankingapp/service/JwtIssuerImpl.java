package com.silversixx.bankingapp.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.silversixx.bankingapp.security.principal.UserPrincipal;
import com.silversixx.bankingapp.security.jwt.JwtProperties;

import com.silversixx.bankingapp.security.jwt.JwtUtils;
import com.silversixx.bankingapp.service.impl.JwtIssuer;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtIssuerImpl implements JwtIssuer {
    private final JwtProperties properties;
    private final JwtUtils jwtUtils;
    @Override
    public String issueAccessToken(UserPrincipal userPrincipal) {
        String authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return JWT.create()
                .withSubject(String.valueOf(userPrincipal.getUsername()))
                .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .withClaim("auth", authorities)
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }

    @Override
    public String issueRefreshToken(UserPrincipal userPrincipal) {
        return JWT.create()
                .withSubject(String.valueOf(userPrincipal.getUsername()))
                .sign(Algorithm.HMAC256(properties.getSecretKey()));
    }

    @Override
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        jwtUtils.extractTokenFromRequest(request)
                .map(jwtUtils::decode)
                .map(jwtUtils::convert)
                .ifPresent(
                        principal -> {
                            String newAccessToken = JWT.create()
                                    .withSubject(principal.getUsername())
                                    .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                                    .withClaim("r", convertAuthoritiesToStrings(principal.getAuthorities()))
                                    .sign(Algorithm.HMAC256(properties.getSecretKey()));
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            JSONObject jsonResponse = new JSONObject();
                            jsonResponse.put("New-Access-Token", newAccessToken);
                            try {
                                response.getWriter().write(jsonResponse.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                );
    }
    private List<String> convertAuthoritiesToStrings(Collection<? extends GrantedAuthority> authorities) {
        List<String> authoritiesAsStrings = new ArrayList<>();
        for (GrantedAuthority authority : authorities) {
            authoritiesAsStrings.add(authority.getAuthority());
        }
        return authoritiesAsStrings;
    }
}