package com.silversixx.bankingapp.service;

import com.silversixx.bankingapp.dao.UserRepository;
import com.silversixx.bankingapp.dto.BankResponse;
import com.silversixx.bankingapp.dto.EmailDetails;
import com.silversixx.bankingapp.dto.RegisterRequest;
import com.silversixx.bankingapp.entity.ConfirmToken;
import com.silversixx.bankingapp.entity.UserModel;
import com.silversixx.bankingapp.service.impl.UserService;
import com.silversixx.bankingapp.utils.AccountUtils;
import com.silversixx.bankingapp.utils.BankResponseUtils;
import com.silversixx.bankingapp.utils.EmailUtils;
import com.silversixx.bankingapp.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.silversixx.bankingapp.security.authorities.Role.*;

@Service
    @Slf4j
    @RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailService;
    private final ConfirmTokenServiceImpl tokenService;
    private final BankResponseUtils bankResponseUtils;
    @Override
    public List<UserModel> fetchAllUsers() {
        return userRepo.findAll();
    }
    @Override
    public BankResponse register(RegisterRequest userRequest) {
        String isValidMailForNew = bankResponseUtils.validateEmailForNewAccount(userRequest.getEmail());
        if(!isValidMailForNew.equals("ok")){
            if(isValidMailForNew.equals(AccountUtils.EMAIL_INVALID_CODE))
                return bankResponseUtils.exceptionResponse(AccountUtils.EMAIL_INVALID_CODE, AccountUtils.EMAIL_INVALID_MESSAGE);
            else
                return bankResponseUtils.exceptionResponse(AccountUtils.ACCOUNT_EXISTS_EMAIL_CODE, AccountUtils.ACCOUNT_EXISTS_EMAIL_MESSAGE);
        }
        String isValidPhoneNumForNew = bankResponseUtils.validatePhoneForNewAccount(userRequest.getPhoneNumber());
        if(!isValidPhoneNumForNew.equals("ok")){
            if(isValidPhoneNumForNew.equals(AccountUtils.INVALID_PHONE_NUMBER_CODE))
                return bankResponseUtils.exceptionResponse(AccountUtils.INVALID_PHONE_NUMBER_CODE, AccountUtils.INVALID_PHONE_NUMBER_MESSAGE);
            else
                return bankResponseUtils.exceptionResponse(AccountUtils.ACCOUNT_EXISTS_PHONE_NUMBER_CODE, AccountUtils.ACCOUNT_EXISTS_PHONE_NUMBER_MESSAGE);
        }
        ConfirmToken confirmToken = createAccount(userRequest);
        String link = "http://localhost:8080/api/v1/user/enable?token=" + confirmToken.getToken();
        emailService.send(
                EmailDetails.builder()
                        .recipientMail(userRequest.getEmail())
                        .subject("ACCOUNT CREATION")
                        .build()
                , EmailUtils.buildConfirmEmail(userRequest.getEmail(),link)
        );
        return bankResponseUtils.actionResponse(
                AccountUtils.ACCOUNT_CREATED_CODE,
                AccountUtils.ACCOUNT_CREATED_MESSAGE,
                userRequest.getFullName(),
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
        switch (isValidConfirmToken) {
            case TokenUtils.TOKEN_NOT_FOUND_CODE -> {
                return bankResponseUtils.exceptionResponse(TokenUtils.TOKEN_NOT_FOUND_CODE, TokenUtils.TOKEN_NOT_FOUND_MESSAGE);
            }
            case TokenUtils.TOKEN_ALREADY_CONFIRMED_CODE -> {
                return bankResponseUtils.exceptionResponse(TokenUtils.TOKEN_ALREADY_CONFIRMED_CODE, TokenUtils.TOKEN_ALREADY_CONFIRMED_MESSAGE);
            }
            case TokenUtils.TOKEN_EXPIRED_CODE -> {
                return bankResponseUtils.exceptionResponse(TokenUtils.TOKEN_EXPIRED_CODE, TokenUtils.TOKEN_EXPIRED_MESSAGE);
            }
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

}
