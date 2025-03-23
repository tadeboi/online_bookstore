package com.bookstore.service;

import com.bookstore.dto.AuthResponse;
import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.SignUpRequest;

public interface AuthService {
    AuthResponse signup(SignUpRequest request);
    AuthResponse login(LoginRequest request);
}