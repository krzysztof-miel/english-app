package com.dev.englishapp.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginDto {
    private String usernameOrEmail;
    private String password;

}
