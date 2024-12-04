package com.dev.englishapp.service;


import com.dev.englishapp.entity.User;
import com.dev.englishapp.model.UserDataDto;
import com.dev.englishapp.model.UserDto;
import com.dev.englishapp.model.UserPreferencesDto;

import java.util.List;

public interface UserService {

    UserDto createUser(User user);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, User updatedUser);
    UserPreferencesDto updateUserPreferences(Long id, UserPreferencesDto preferences);
    UserDataDto updateUserData(Long id, UserDataDto data);
    void deleteUser(Long id);

}
