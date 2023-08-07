package com.silversixx.bankingapp.service.impl;

import com.silversixx.bankingapp.dto.EmailDetails;

public interface EmailService {
    void send(EmailDetails emailDetails, String formEmail);
}
