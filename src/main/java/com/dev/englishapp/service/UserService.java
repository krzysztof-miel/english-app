package com.dev.englishapp.service;

import com.dev.englishapp.entity.User;
import com.dev.englishapp.entity.UserPreferences;
import com.dev.englishapp.model.UserDto;
import com.dev.englishapp.model.UserPreferencesDto;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface UserService {

    UserDto createUser(User user);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, User updatedUser) throws AccessDeniedException;
    UserPreferencesDto setUserPreferences(Long id, UserPreferences preferences);
    void deleteUser(Long id);
    String getOpenAiResponseForUser(Long id) throws IOException;

    String generateWordsUsingJWT(String userEmail) throws IOException;

    UserPreferencesDto updateUserPreferenceUsingJWT(String userEmail, UserPreferencesDto preferencesDto);

    UserDto getCurrentUser(String email);

}
