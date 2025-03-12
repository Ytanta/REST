package org.example.service;

import org.example.dao.UserDAO;
import org.example.dto.UserDTO;
import org.example.model.User;
import org.example.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        return toDTO(user);
    }

    public void createUser(UserDTO userDTO) {
        User user = toEntity(userDTO);
        userRepository.save(user);
    }

    public void updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id);
        if (existingUser == null) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        existingUser.setName(userDTO.getName());
        userRepository.update(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    private UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getId(),
                user.getName()
        );
    }

    private User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        return new User(
                dto.getId(),
                dto.getName()
        );
    }
}