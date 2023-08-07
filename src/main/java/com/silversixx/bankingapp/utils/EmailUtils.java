package com.silversixx.bankingapp.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailUtils implements Predicate<String> {
    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE
    );
    @Override
    public boolean test(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.matches();
    }
    public static String buildConfirmEmail(String recipient, String confirmLink){
        return "<div class=\"container\" style=\"font-family: Arial, sans-serif; background-color: #f1f1f1; margin: 0; padding: 0; max-width: 600px; margin: 0 auto; background-color: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">\n" +
                "    <h1 style=\"color: #007bff; margin-bottom: 20px;\">ACCOUNT CREATED SUCCESSFULLY</h1>\n" +
                "    <p style=\"margin-bottom: 10px;\">Dear " + recipient + ",</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">Thank you for choosing our service. Please click the button below to confirm your email address:</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">\n" +
                "        <a class=\"button\" href=\"" + confirmLink + "\" style=\"display: inline-block; background-color: #007bff; color: #fff; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Confirm</a>\n" +
                "    </p>\n" +
                "    <p style=\"margin-bottom: 10px;\">Best regards,</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">LDeBanking Team</p>\n" +
                "</div>\n" +
                "<style>\n" +
                "@media only screen and (max-width: 600px) {\n" +
                "    .container {\n" +
                "        max-width: 100% !important;\n" +
                "        padding: 10px !important;\n" +
                "    }\n" +
                "    h1 {\n" +
                "        font-size: 24px !important;\n" +
                "    }\n" +
                "    .button {\n" +
                "        padding: 8px 16px !important;\n" +
                "    }\n" +
                "}\n" +
                "</style>";
    }
    public static String buildTransactionalDetails( LocalDateTime date,
                                                    String transactionType,
                                                    String sourceAccount,
                                                    BigDecimal sourceAccountBalance,
                                                    String destinationAccount,
                                                    BigDecimal amount,
                                                    String message
    ){
        return  "<tr>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ date +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ transactionType +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ sourceAccount +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">$"+ sourceAccountBalance.toString() +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ destinationAccount +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">$"+ amount.toString() +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ message +"</td>\n" +
                "</tr>\n";

    }
    public static String buildCreditDebitDetails( LocalDateTime date,
                                                    String transactionType,
                                                    String accountNumber,
                                                    BigDecimal sourceAccountBalance,
                                                    BigDecimal amount
    ){
        return  "<tr>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ date +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ transactionType +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ accountNumber +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">$"+ sourceAccountBalance.toString() +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">$"+ amount.toString() +"</td>\n" +
                "</tr>\n";

    }
    public static String buildCreditDebitEmail(String recipient, String transactionDetails) {
        return "<div class=\"container\" style=\"font-family: Arial, sans-serif; background-color: #f1f1f1; margin: 0; padding: 0; max-width: 600px; margin: 0 auto; background-color: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">\n" +
                "    <h1 style=\"color: #007bff; margin-bottom: 20px;\">TRANSACTION NOTIFICATION</h1>\n" +
                "    <p style=\"margin-bottom: 10px;\">Dear " + recipient + ",</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">We would like to inform you about the recent transaction made on your account. Below are the transaction details:</p>\n" +
                "    <table style=\"width: 100%; border-collapse: collapse;\">\n" +
                "        <tr>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Date</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Description</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Bank Account</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Balance</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Amount</th>\n" +
                "        </tr>\n" +
                transactionDetails +
                "    </table>\n" +
                "    <p style=\"margin-bottom: 10px;\">If you did not perform this transaction or have any concerns, please contact our support immediately.</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">Best regards,</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">LDeBanking Team</p>\n" +
                "</div>\n" +
                "<style>\n" +
                "@media only screen and (max-width: 600px) {\n" +
                "    .container {\n" +
                "        max-width: 100% !important;\n" +
                "        padding: 10px !important;\n" +
                "    }\n" +
                "    h1 {\n" +
                "        font-size: 24px !important;\n" +
                "    }\n" +
                "    table {\n" +
                "        font-size: 14px !important;\n" +
                "    }\n" +
                "    th,\n" +
                "    td {\n" +
                "        padding: 6px 8px !important;\n" +
                "    }\n" +
                "    .button {\n" +
                "        padding: 8px 16px !important;\n" +
                "    }\n" +
                "}\n" +
                "</style>";
    }
    public static String buildTransactionEmail(String recipient, String transactionDetails) {
        return "<div class=\"container\" style=\"font-family: Arial, sans-serif; background-color: #f1f1f1; margin: 0; padding: 0; max-width: 600px; margin: 0 auto; background-color: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">\n" +
                "    <h1 style=\"color: #007bff; margin-bottom: 20px;\">TRANSACTION NOTIFICATION</h1>\n" +
                "    <p style=\"margin-bottom: 10px;\">Dear " + recipient + ",</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">We would like to inform you about the recent transaction made on your account. Below are the transaction details:</p>\n" +
                "    <table style=\"width: 100%; border-collapse: collapse;\">\n" +
                "        <tr>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Date</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Description</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Debit account</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Balance</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Credit account</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Amount</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Message</th>\n" +
                "        </tr>\n" +
                transactionDetails +
                "    </table>\n" +
                "    <p style=\"margin-bottom: 10px;\">If you did not perform this transaction or have any concerns, please contact our support immediately.</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">Best regards,</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">LDeBanking Team</p>\n" +
                "</div>\n" +
                "<style>\n" +
                "@media only screen and (max-width: 600px) {\n" +
                "    .container {\n" +
                "        max-width: 100% !important;\n" +
                "        padding: 10px !important;\n" +
                "    }\n" +
                "    h1 {\n" +
                "        font-size: 24px !important;\n" +
                "    }\n" +
                "    table {\n" +
                "        font-size: 14px !important;\n" +
                "    }\n" +
                "    th,\n" +
                "    td {\n" +
                "        padding: 6px 8px !important;\n" +
                "    }\n" +
                "    .button {\n" +
                "        padding: 8px 16px !important;\n" +
                "    }\n" +
                "}\n" +
                "</style>";
    }
    public static String buildBenefitedDetails( LocalDateTime date,
                                                String transactionType,
                                                String sourceAccount,
                                                BigDecimal sourceAccountBalance,
                                                String destinationAccount,
                                                BigDecimal amount,
                                                String message
    ){
        return  "<tr>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ date +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ transactionType +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ sourceAccount +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ destinationAccount +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">$"+ sourceAccountBalance.toString() +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">$"+ amount.toString() +"</td>\n" +
                "    <td style=\"border: 1px solid #ccc; padding: 8px;\">"+ message +"</td>\n" +
                "</tr>\n";

    }
    public static String buildBenefitedTransactionsEmail(String recipient, String transactionDetails){
        return "<div class=\"container\" style=\"font-family: Arial, sans-serif; background-color: #f1f1f1; margin: 0; padding: 0; max-width: 600px; margin: 0 auto; background-color: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">\n" +
                "    <h1 style=\"color: #007bff; margin-bottom: 20px;\">TRANSACTION NOTIFICATION</h1>\n" +
                "    <p style=\"margin-bottom: 10px;\">Dear " + recipient + ",</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">We would like to inform you about the recent transaction made on your account. Below are the transaction details:</p>\n" +
                "    <table style=\"width: 100%; border-collapse: collapse;\">\n" +
                "        <tr>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Date</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Description</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Debit account</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Credit account</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Balance</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Amount Received</th>\n" +
                "            <th style=\"border: 1px solid #ccc; padding: 8px;\">Message</th>\n" +
                "        </tr>\n" +
                transactionDetails +
                "    </table>\n" +
                "    <p style=\"margin-bottom: 10px;\">If you did not perform this transaction or have any concerns, please contact our support immediately.</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">Best regards,</p>\n" +
                "    <p style=\"margin-bottom: 10px;\">LDeBanking Team</p>\n" +
                "</div>\n" +
                "<style>\n" +
                "@media only screen and (max-width: 600px) {\n" +
                "    .container {\n" +
                "        max-width: 100% !important;\n" +
                "        padding: 10px !important;\n" +
                "    }\n" +
                "    h1 {\n" +
                "        font-size: 24px !important;\n" +
                "    }\n" +
                "    table {\n" +
                "        font-size: 14px !important;\n" +
                "    }\n" +
                "    th,\n" +
                "    td {\n" +
                "        padding: 6px 8px !important;\n" +
                "    }\n" +
                "    .button {\n" +
                "        padding: 8px 16px !important;\n" +
                "    }\n" +
                "}\n" +
                "</style>";
    }
}
