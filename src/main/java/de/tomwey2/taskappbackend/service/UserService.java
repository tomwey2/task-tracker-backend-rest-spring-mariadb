package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.model.User;
import de.tomwey2.taskappbackend.dto.UserResponseDto;
import de.tomwey2.taskappbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto createUser(User user) {
        user.setId(null);
        // Hier würde man normalerweise das Passwort hashen
        return convertToDto(userRepository.save(user));
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    private UserResponseDto convertToDto(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getCreatedAt(), user.getUpdatedAt());

    }
}