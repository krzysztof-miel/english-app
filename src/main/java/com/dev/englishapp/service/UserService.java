package com.dev.englishapp.service;


import com.dev.englishapp.entity.User;
import com.dev.englishapp.model.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(User user);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, User updatedUser);
    void deleteUser(Long id);

}
