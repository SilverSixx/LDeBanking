package com.silversixx.bankingapp.service;

import com.silversixx.bankingapp.dao.ConfirmTokenRepo;
import com.silversixx.bankingapp.service.impl.ConfirmTokenService;
import com.silversixx.bankingapp.entity.ConfirmToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ConfirmTokenServiceImpl implements ConfirmTokenService {
    private final ConfirmTokenRepo repo;
    @Override
    public void saveToken(ConfirmToken token) {
        repo.save(token);
    }
    @Override
    public Optional<ConfirmToken> getToken(String token) {
        return repo.findByToken(token);
    }
    @Override
    public void setConfirmedAt(String token) {
        repo.updateConfirmedAt(
                token,
                LocalDateTime.now()
        );
    }
}
