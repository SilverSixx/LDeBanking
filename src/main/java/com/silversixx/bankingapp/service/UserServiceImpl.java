package com.silversixx.bankingapp.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.silversixx.bankingapp.dao.UserRepository;
import com.silversixx.bankingapp.dto.BankResponse;
import com.silversixx.bankingapp.dto.EmailDetails;
import com.silversixx.bankingapp.dto.RegisterRequest;
import com.silversixx.bankingapp.entity.ConfirmToken;
import com.silversixx.bankingapp.entity.UserModel;
import com.silversixx.bankingapp.utils.JwtProperties;
import com.silversixx.bankingapp.entity.principal.UserDetailsServiceImpl;
import com.silversixx.bankingapp.entity.principal.UserPrincipal;
import com.silversixx.bankingapp.service.impl.UserService;
import com.silversixx.bankingapp.utils.AccountUtils;
import com.silversixx.bankingapp.utils.BankResponseUtils;
import com.silversixx.bankingapp.utils.EmailUtils;
import com.silversixx.bankingapp.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;

import static com.silversixx.bankingapp.security.authorities.Role.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailService;
    private final ConfirmTokenServiceImpl tokenService;
    private final BankResponseUtils bankResponseUtils;
    private final JwtProperties properties;
    @Override
    public List<UserModel> fetchAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public BankResponse register(RegisterRequest request) {
        String isValidCredentials = bankResponseUtils.validateCredentialsForNewAccount(request);
        if(!isValidCredentials.equals("ok")){
            return bankResponseUtils.handleAccountValidationResponse(isValidCredentials);
        }
        ConfirmToken confirmToken = createAccount(request);
        String link = "http://localhost:8080/api/v1/user/enable?token=" + confirmToken.getToken();
        emailService.send(
                EmailDetails.builder()
                        .recipientMail(request.getEmail())
                        .subject("ACCOUNT CREATION")
                        .build()
                , EmailUtils.buildConfirmEmail(request.getEmail(),link)
        );
        return bankResponseUtils.actionResponse(
                AccountUtils.ACCOUNT_CREATED_CODE,
                AccountUtils.ACCOUNT_CREATED_MESSAGE,
                request.getFullName(),
                confirmToken.getUser().getAccountNumber(),
                BigDecimal.ZERO
        );
    }
    @Override
    public ConfirmToken createAccount(RegisterRequest userRequest) {
        UserModel userFromRequest = UserModel.builder()
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .fullName(userRequest.getFullName())
                .dob(userRequest.getDob())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .phoneNumber(userRequest.getPhoneNumber())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .enabled(false)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(Set.of(USER))
                .build();
        userRepo.save(userFromRequest);
        String token = UUID.randomUUID().toString();
        ConfirmToken confirmationToken = ConfirmToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(userFromRequest)
                .build();
        tokenService.saveToken(confirmationToken);
        return confirmationToken;
    }
    @Override
    public BankResponse confirm(String token) {
        ConfirmToken confirmToken = tokenService.getToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        String isValidConfirmToken = TokenUtils.validateTokenForConfirmation(confirmToken);
        if(!isValidConfirmToken.equals("ok")){
            return bankResponseUtils.handleTokenValidationResponse(isValidConfirmToken);
        }
        tokenService.setConfirmedAt(token);
        enableAccount(confirmToken.getUser().getEmail());
        return bankResponseUtils.actionResponse(
                AccountUtils.ACCOUNT_CONFIRMED_CODE,
                AccountUtils.ACCOUNT_CONFIRMED_MESSAGE,
                confirmToken.getUser().getFullName(),
                confirmToken.getUser().getAccountNumber(),
                confirmToken.getUser().getAccountBalance()
        );
    }
    @Override
    public void enableAccount(String email) {
        userRepo.enableUser(email);
    }
    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTHORIZATION);
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            Algorithm algorithm = Algorithm.HMAC256(properties.getSecretKey().getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(decodedJWT.getSubject());
            String newAccessToken = JWT.create()
                    .withSubject(principal.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .withClaim("authorities",
                            principal.getAuthorities()
                                    .stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList())
                    )
                    .withClaim("password", principal.getPassword())
                    .sign(algorithm);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("New-Access-Token", newAccessToken);
            try {
                response.getWriter().write(jsonResponse.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalStateException("Invalid auth header.");
        }
    }

}
