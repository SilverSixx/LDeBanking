package com.silversixx.bankingapp.service.impl;

import com.silversixx.bankingapp.dto.*;
import com.silversixx.bankingapp.entity.ConfirmToken;
import com.silversixx.bankingapp.entity.UserModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
public interface UserService {
    List<UserModel> fetchAllUsers();
    BankResponse register(RegisterRequest request);
    BankResponse confirm (String token);
    void enableAccount(String email);
    ConfirmToken createAccount(RegisterRequest userRequest);
    void refreshToken(HttpServletRequest request, HttpServletResponse response);
}
