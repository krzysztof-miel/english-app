package com.dev.englishapp.controller;


import com.dev.englishapp.entity.User;
import com.dev.englishapp.entity.UserPreferences;
import com.dev.englishapp.model.UserDto;
import com.dev.englishapp.model.UserPreferencesDto;
import com.dev.englishapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody User user) {
        UserDto createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody User updatedUser) throws AccessDeniedException {
        UserDto updated = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<UserPreferencesDto> setUserPreferences(@PathVariable Long id, @RequestBody UserPreferences preferences) {
        UserPreferencesDto updatedPreferences = userService.setUserPreferences(id, preferences);
        return ResponseEntity.ok(updatedPreferences);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ask")
    public ResponseEntity<String> getChatResponseForUserPreference(@PathVariable Long id) throws IOException {
        String response = userService.getOpenAiResponseForUser(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generateWords")
    public ResponseEntity<String> getChatResponseForUserPreference(Authentication authentication) throws IOException {
        String generatedWords = userService.generateWordsUsingJWT(authentication.getName()); // przesyłanie maila do metody w serwisie
        return ResponseEntity.ok(generatedWords);
    }

    @PostMapping("/preferences")
    public ResponseEntity<UserPreferencesDto> updateUserPreferences(@RequestBody UserPreferencesDto preferencesDto, Authentication authentication) {

        UserPreferencesDto updatedPreferences = userService.updateUserPreferenceUsingJWT(authentication.getName(), preferencesDto);
        return ResponseEntity.ok(updatedPreferences);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        UserDto userDto = userService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(userDto);
    }



}
