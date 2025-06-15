package com.immortals.authapp.service;

import com.immortals.authapp.model.dto.LoginDto;
import com.immortals.authapp.model.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginDto loginDto);
    String generateRefreshToken(String username);

    void logout();
}