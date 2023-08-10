package com.silversixx.bankingapp.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountUtils {
    /**
     * 1 error
     * 2 success
     */
    public static final String ACCOUNT_EXISTS_EMAIL_CODE = "001";
    public static final String ACCOUNT_EXISTS_EMAIL_MESSAGE = "This email already belongs to an account.";
    public static final String ACCOUNT_EXISTS_CODE = "000";
    public static final String ACCOUNT_EXISTS_MESSAGE = "Account already exists, please register again.";
    public static final String ACCOUNT_REGISTERED_NOT_ENABLED_CODE = "111";
    public static final String ACCOUNT_REGISTERED_NOT_ENABLED_MESSAGE = "Please enable your account";
    public static final String ACCOUNT_NOT_EXISTS_CODE = "011";
    public static final String ACCOUNT_NOT_EXISTS_MESSAGE = "This account does not exits.";
    public static final String EMAIL_INVALID_CODE = "010";
    public static final String EMAIL_INVALID_MESSAGE = "Your request email is invalid, please use different email.";
    public static final String INSUFFICIENT_BALANCE_CODE = "110";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "The balance in account is smaller than the request amount.";
    public static final String INVALID_OTP_CODE = "100";
    public static final String INVALID_OTP_MESSAGE = "Please carefully send correct received otp.";
    public static final String INVALID_PHONE_NUMBER_CODE = "100";
    public static final String INVALID_PHONE_NUMBER_MESSAGE = "Invalid phone number.";
    public static final String ACCOUNT_CREATED_CODE = "002";
    public static final String ACCOUNT_CREATED_MESSAGE = "Create new account successfully.";
    public static final String ACCOUNT_FOUND_CODE = "022";
    public static final String ACCOUNT_FOUND_MESSAGE = "Found requested account.";
    public static final String ACCOUNT_CONFIRMED_CODE = "202";
    public static final String ACCOUNT_CONFIRMED_MESSAGE = "Account confirmed.";
    public static final String ACCOUNT_CREDIT_CODE = "222";
    public static final String ACCOUNT_CREDIT_MESSAGE = "Account credit successfully.";
    public static final String ACCOUNT_DEBIT_SUCCESS_CODE = "020";
    public static final String ACCOUNT_DEBIT_SUCCESS_MESSAGE = "Account debit successfully.";
    public static final String SUCCESSFUL_TRANSACTION_CODE = "200";
    public static final String SUCCESSFUL_TRANSACTION_MESSAGE = "Transfer %s to account %s (%s) successfully. With message: %s";
    private static final Pattern VALID_PHONE_NUMBER_PATTERN = Pattern.compile(
            "^(?:\\+84|0)(?:3[2-9]|5[2689]|7[0|6-9]|8[1-9]|9[0-9])\\d{7}$", Pattern.CASE_INSENSITIVE
    );

    public static boolean testPhoneNumber(String phoneNumber){
        Matcher matcher = VALID_PHONE_NUMBER_PATTERN.matcher(phoneNumber);
        return matcher.matches();
    }

    public static String generateAccountNumber(){
        int preNums = 2003;
        Random random = new Random();
        int randomNumber = random.nextInt(9000) + 1000;
        String lastNums = String.valueOf(preNums * randomNumber);
        String first4Nums = String.valueOf(preNums);
        String sec4Nums = String.valueOf(randomNumber);
        String last4Nums =  lastNums.substring(lastNums.length() - 4);
        return first4Nums + sec4Nums + last4Nums;
    }
}
