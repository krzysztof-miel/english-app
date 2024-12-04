package com.dev.englishapp.model;

import java.time.LocalTime;
import lombok.Data;

@Data
public class UserPreferencesDto {
    private Integer wordCountPreference;
    private LocalTime timePreference;
}
