package com.silversixx.bankingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetails {
    private LocalDateTime time;
    private String description;
    private String recipientMail;
    private String messageBody;
    private String subject;
}
