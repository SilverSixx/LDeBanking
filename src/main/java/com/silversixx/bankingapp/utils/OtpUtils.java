package com.silversixx.bankingapp.utils;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class OtpUtils {
    private static final long EXPIRATION_DURATION_MINUTES = 5;
    private final Map<String, OTPData> otpMap = new HashMap<>();

    public String generateAndStoreOTP(String accountNumber) {
        String otp = generateOTP();
        long expirationTime = System.currentTimeMillis() + (EXPIRATION_DURATION_MINUTES * 60 * 1000);
        otpMap.put(accountNumber, new OTPData(otp, expirationTime));
        return otp;
    }

    public boolean verifyOTP(String userInputOTP, String accountNumber) {
        OTPData otpData = otpMap.get(accountNumber);
        if (otpData != null) {
            if (System.currentTimeMillis() <= otpData.expirationTime()) {
                if (otpData.otp().equals(userInputOTP)) {
                    otpMap.remove(accountNumber); // OTP is used, remove it from the map
                    return true;
                }
            } else {
                otpMap.remove(accountNumber); // OTP has expired, remove it from the map
            }
        }
        return false;
    }

    private String generateOTP() {
        return new DecimalFormat("00000000").format(new Random().nextInt(99999999));
    }

    private class OTPData {
        private final String otp;
        private final long expirationTime;

        public OTPData(String otp, long expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }

        public String otp() {
            return otp;
        }

        public long expirationTime() {
            return expirationTime;
        }
    }
}
