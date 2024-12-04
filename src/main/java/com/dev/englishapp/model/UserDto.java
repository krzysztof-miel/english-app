package com.dev.englishapp.model;

import lombok.Data;
import java.time.LocalTime;

@Data
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private Integer wordCountPreference;
    private LocalTime timePreference;

}
