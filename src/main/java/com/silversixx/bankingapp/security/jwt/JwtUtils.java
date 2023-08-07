package com.silversixx.bankingapp.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.silversixx.bankingapp.security.principal.UserDetailsServiceImpl;
import com.silversixx.bankingapp.security.principal.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final JwtProperties properties;
    private final UserDetailsServiceImpl userService;
    public UserPrincipal convert(DecodedJWT jwt){
        return (UserPrincipal) userService.loadUserByUsername(jwt.getSubject());
    }
    public DecodedJWT decode(String token){
        return JWT
                .require(Algorithm.HMAC256(properties.getSecretKey()))
                .build()
                .verify(token);
    }
    public Optional<String> extractTokenFromRequest(HttpServletRequest request){
        var token = request.getHeader("Authorization");
        if(StringUtils.hasText(token) && token.startsWith("Bearer ")){
            return Optional.of(token.substring(7));
        }
        return Optional.empty();
    }


    public Date extractExpirations(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    public Boolean hasClaim(String token, String claimName){
        Claims claims = extractAllClaims(token);
        return claims.get(claimName) != null;
    }
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    public Claims extractAllClaims (String token){
        return Jwts.parser().setSigningKey(properties.getSecretKey()).parseClaimsJwt(token).getBody();
    }

    public Boolean isTokenExpired(String token){
        return extractExpirations(token).before(new Date());
    }
    public String generateToken(UserDetails userDetails){
        Map<String , Object> claims = new HashMap<>();
        return createToken(claims, userDetails);
    }
    public String generateToken(UserDetails userDetails, Map<String, Object> claims){
        return createToken(claims, userDetails);
    }

    private String createToken(Map<String, Object> claims, UserDetails userDetails){
        return Jwts.builder().setClaims(claims)
                .setSubject(userDetails.getUsername())
                .claim("authorities", userDetails.getAuthorities())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)))
                .signWith(SignatureAlgorithm.HS256, properties.getSecretKey()).compact();
    }
    public Boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
