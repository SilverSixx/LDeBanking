package com.silversixx.bankingapp.service;

import com.silversixx.bankingapp.dto.EmailDetails;
import com.silversixx.bankingapp.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Override
    public void send(EmailDetails emailDetails, String formEmail) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper  = new MimeMessageHelper(mimeMessage,"utf-8");
            helper.setText(formEmail, true);
            helper.setTo(emailDetails.getRecipientMail());
            helper.setSubject(emailDetails.getSubject());
            helper.setFrom(senderEmail);
            mailSender.send(mimeMessage);
            log.info("Mail sent successfully.");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

}
