package com.dev.englishapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private LocalTime timePreference;

    private Integer wordCountPreference;

    public void setWordCountPreference(int wordCountPreference) {
        if (wordCountPreference == 5 || wordCountPreference == 8 || wordCountPreference == 10) {
            this.wordCountPreference = wordCountPreference;
        } else {
            throw new IllegalArgumentException("Word count preference must be 5, 8, or 10.");
        }
    }

}
