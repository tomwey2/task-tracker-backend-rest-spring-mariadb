package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.dto.UserRequestDto;
import de.tomwey2.taskappbackend.exception.ResourceNotFoundException;
import de.tomwey2.taskappbackend.model.User;
import de.tomwey2.taskappbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(UserRequestDto userRequestDto) {
        User newUser = new User();
        newUser.setUsername(userRequestDto.username());
        newUser.setPassword(userRequestDto.password());
        newUser.setEmail(userRequestDto.email());
        newUser.setRole(userRequestDto.role());
        // Hier würde man normalerweise das Passwort hashen
        return userRepository.save(newUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // In service/UserService.java
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}