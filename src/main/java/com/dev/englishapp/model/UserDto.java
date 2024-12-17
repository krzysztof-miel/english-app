package com.dev.englishapp.model;

import com.dev.englishapp.entity.UserPreferences;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private UserPreferences preferences;

}
