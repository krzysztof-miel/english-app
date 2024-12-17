package com.dev.englishapp.model;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPreferencesDto {

    private LocalTime timePreference;
    private Integer wordCountPreference;
}
