package com.dev.englishapp.service;

import com.dev.englishapp.model.LoginDto;
import com.dev.englishapp.model.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);

}
