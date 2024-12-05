package com.dev.englishapp.service;

import com.dev.englishapp.entity.User;
import com.dev.englishapp.exception.UserAlreadyExistsException;
import com.dev.englishapp.exception.UserNotFoundException;
import com.dev.englishapp.model.UserDataDto;
import com.dev.englishapp.model.UserDto;
import com.dev.englishapp.model.UserPreferencesDto;
import com.dev.englishapp.openAiClient.OpenAiClient;
import com.dev.englishapp.openAiClient.Prompt;
import com.dev.englishapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OpenAiClient openAiClient;

    @Override
    public UserDto createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User with the same username or email already exists.");
        }
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(this::mapToDto)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    @Override
    public UserDto updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());

        user.setWordCountPreference(updatedUser.getWordCountPreference());
        user.setTimePreference(updatedUser.getTimePreference());

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserPreferencesDto updateUserPreferences(Long id, UserPreferencesDto preferences) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        user.setWordCountPreference(preferences.getWordCountPreference());
        user.setTimePreference(preferences.getTimePreference());

        userRepository.save(user);
        return preferences;
    }

    @Override
    public UserDataDto updateUserData(Long id, UserDataDto data) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        user.setUsername(data.getUsername());
        user.setEmail(data.getEmail());
        user.setPassword(data.getPassword());


        userRepository.save(user);
        return data;
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

        int userPreferenceNumber = user.getWordCountPreference();
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

//        OpenAiClient client = new OpenAiClient();

        return openAiClient.getResponse(promptWithPreference);
    }

    private UserDto mapToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setTimePreference(user.getTimePreference());
        userDto.setWordCountPreference(user.getWordCountPreference());
        return userDto;
    }
}
