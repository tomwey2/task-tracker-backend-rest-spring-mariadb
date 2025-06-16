package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.model.Task;
import de.tomwey2.taskappbackend.model.TaskDto;
import de.tomwey2.taskappbackend.model.User;
import de.tomwey2.taskappbackend.model.UserDto;
import de.tomwey2.taskappbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto createUser(User user) {
        user.setId(null);
        // Hier würde man normalerweise das Passwort hashen
        return convertToDto(userRepository.save(user));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    private UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getUsername());

    }
}