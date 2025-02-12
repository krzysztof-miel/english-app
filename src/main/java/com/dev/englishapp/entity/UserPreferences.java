package com.dev.englishapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Data
@Entity
@Table(name = "user-preferences")
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime timePreference = LocalTime.of(8, 0);

    @Column(nullable = false)
    private Integer wordCountPreference = 5;

    public void setWordCountPreference(int wordCountPreference) {
        if (wordCountPreference == 5 || wordCountPreference == 8 || wordCountPreference == 10) {
            this.wordCountPreference = wordCountPreference;
        } else {
            this.wordCountPreference =5;
            throw new IllegalArgumentException("Word count preference must be 5, 8, or 10.");
        }
    }
}
