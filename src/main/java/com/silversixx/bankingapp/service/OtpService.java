package com.silversixx.bankingapp.service;

import com.silversixx.bankingapp.utils.OtpProperties;
import com.silversixx.bankingapp.dto.OtpResponse;
import com.silversixx.bankingapp.entity.UserModel;
import com.silversixx.bankingapp.utils.OtpStatus;
import com.silversixx.bankingapp.dao.UserRepository;
import com.silversixx.bankingapp.utils.OtpUtils;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpProperties otpProperties;
    private final UserRepository userRepo;
    private final OtpUtils otpUtils;
    public Mono<OtpResponse> sendOTP(String accountNumber) {
        return sendOTPInternal(accountNumber)
                .map(otp -> new OtpResponse(OtpStatus.DELIVERED, "OTP sent successfully"))
                .onErrorResume(ex -> Mono.just(new OtpResponse(OtpStatus.FAILED, ex.getMessage())));
    }
    private Mono<String> sendOTPInternal(String accountNumber) {
        return Mono.fromCallable(() -> {
            UserModel userOpt = userRepo.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String phoneNumber = userOpt.getPhoneNumber();
            String otp = otpUtils.generateAndStoreOTP(accountNumber);
            String otpMessage = "Your OTP is " + otp + ". Use this to complete your transaction. Thank You.";
            Message.creator(new com.twilio.type.PhoneNumber(phoneNumber), new com.twilio.type.PhoneNumber(otpProperties.getTrialNumber()), otpMessage).create();
            return otp;
        });
    }
}
