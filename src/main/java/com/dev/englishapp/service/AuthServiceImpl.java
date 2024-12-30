package com.dev.englishapp.service;

import com.dev.englishapp.entity.Role;
import com.dev.englishapp.entity.User;
import com.dev.englishapp.exception.AppAPIException;
import com.dev.englishapp.model.LoginDto;
import com.dev.englishapp.model.RegisterDto;
import com.dev.englishapp.repository.RoleRepository;
import com.dev.englishapp.repository.UserRepository;
import com.dev.englishapp.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService{

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;

    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    public String login(LoginDto loginDto) {

         Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        System.out.println("User Logged-in successfully!");

        return token;
    }

    @Override
    public String register(RegisterDto registerDto) {

        if (userRepository.existsByUsername(registerDto.getUsername())){
            throw new AppAPIException(HttpStatus.BAD_REQUEST, "Username already exists!");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())){
            throw new AppAPIException(HttpStatus.BAD_REQUEST, "Email already exists!");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return "User registered successfully!";
    }
}
