package com.silversixx.bankingapp.controller;

import com.silversixx.bankingapp.dto.*;
import com.silversixx.bankingapp.entity.UserModel;
import com.silversixx.bankingapp.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl bankUserService;
    @GetMapping("/fetchAll")
    @PreAuthorize("hasAuthority('user:read')")
    public List<UserModel> fetchAllUsers(){return bankUserService.fetchAllUsers();}
    @PostMapping("/new")
    public BankResponse createNewAccount(@RequestBody RegisterRequest userRequest){
        return bankUserService.register(userRequest);
    }
    @GetMapping("/enable")
    public BankResponse confirm(@RequestParam("token") String token){
        return bankUserService.confirm(token);
    }
}
