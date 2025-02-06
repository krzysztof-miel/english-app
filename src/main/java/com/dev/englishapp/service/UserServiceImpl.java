package com.dev.englishapp.service;

import com.dev.englishapp.entity.User;
import com.dev.englishapp.entity.UserPreferences;
import com.dev.englishapp.exception.UserAlreadyExistsException;
import com.dev.englishapp.exception.UserNotFoundException;
import com.dev.englishapp.model.UserDto;
import com.dev.englishapp.model.UserPreferencesDto;
import com.dev.englishapp.openAiClient.OpenAiClient;
import com.dev.englishapp.openAiClient.Prompt;
import com.dev.englishapp.repository.UserPreferencesRepository;
import com.dev.englishapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;


import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private OpenAiClient openAiClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User with the same username or email already exists.");
        }
        User savedUser = userRepository.save(user);
        return mapToUserDto(savedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(this::mapToUserDto)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    @Override
    public UserDto updateUser(Long id, User updatedUser) throws AccessDeniedException {

        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(loggedInUsername);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!user.getEmail().equals(loggedInUsername)) {
            throw new AccessDeniedException("You are not allowed to update this user's data.");
        }

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
//        user.setPassword(updatedUser.getPassword());

        User savedUser = userRepository.save(user);
        return mapToUserDto(savedUser);
    }

    @Override
    public UserPreferencesDto setUserPreferences(Long id, UserPreferences preferences) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        UserPreferences userPreferences = user.getPreferences();

        if (userPreferences == null) {
            System.out.println("UStawiam preferencje");
            userPreferences = new UserPreferences();
            userPreferences.setTimePreference(preferences.getTimePreference());
            userPreferences.setWordCountPreference(preferences.getWordCountPreference());
        }

        userPreferences.setTimePreference(preferences.getTimePreference());
        userPreferences.setWordCountPreference(preferences.getWordCountPreference());

        user.setPreferences(userPreferences);

        userRepository.save(user);
        return mapToUserPreferencesDto(userPreferences);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found.");
        }
        userRepository.deleteById(id);
    }

    @Override
    public String getOpenAiResponseForUser(Long id) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        int userPreferenceNumber = user.getPreferences().getWordCountPreference();
        String promptWithPreference;

        switch (userPreferenceNumber) {
            case 8:
                promptWithPreference = Prompt.EIGHT.getPrompt();
                System.out.println(promptWithPreference);
                break;
            case 10:
                promptWithPreference = Prompt.TEN.getPrompt();
                System.out.println(promptWithPreference);
                break;
            default:
                promptWithPreference = Prompt.FIVE.getPrompt();
                System.out.println(promptWithPreference);
                break;
        }

        return openAiClient.getResponse(promptWithPreference);
    }

    @Override
    public UserDto getCurrentUser(String email) {
        System.out.println(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        return mapToUserDto(user);
    }

    @Override
    public String generateWordsUsingJWT(String userEmail) throws IOException {
        System.out.println(userEmail);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        int userPreferenceNumber = user.getPreferences().getWordCountPreference();
        String promptWithPreference;

        switch (userPreferenceNumber) {
            case 8:
                promptWithPreference = Prompt.EIGHT.getPrompt();
                System.out.println(promptWithPreference);
                break;
            case 10:
                promptWithPreference = Prompt.TEN.getPrompt();
                System.out.println(promptWithPreference);
                break;
            default:
                promptWithPreference = Prompt.FIVE.getPrompt();
                System.out.println(promptWithPreference);
                break;
        }

        return openAiClient.getResponse(promptWithPreference);
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setPreferences(user.getPreferences());
        return userDto;
    }

    private UserPreferencesDto mapToUserPreferencesDto(UserPreferences userPreferences){
        UserPreferencesDto userPreferencesDto = new UserPreferencesDto();
        userPreferencesDto.setTimePreference(userPreferences.getTimePreference());
        userPreferencesDto.setWordCountPreference(userPreferences.getWordCountPreference());
        return userPreferencesDto;
    }



}
