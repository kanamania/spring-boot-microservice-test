package tz.co.flex.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tz.co.flex.payment.dto.CreateUserRequest;
import tz.co.flex.payment.dto.UpdateUserRequest;
import tz.co.flex.payment.dto.UserDto;
import tz.co.flex.payment.model.Role;
import tz.co.flex.payment.model.RoleEntity;
import tz.co.flex.payment.model.User;
import tz.co.flex.payment.repository.RoleRepository;
import tz.co.flex.payment.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByUsernameAndDeletedFalse(request.username())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.existsByEmailAndDeletedFalse(request.email())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User(
            request.name(),
            request.username(),
            request.email(),
            passwordEncoder.encode(request.password())
        );
        
        Set<RoleEntity> roles = roleRepository.findByNameIn(request.roles());
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    @Transactional
    public List<UserDto> getAllUsers(boolean includeDeleted) {
        List<User> users = includeDeleted ? 
            userRepository.findAll() : 
            userRepository.findAll().stream()
                .filter(user -> !user.isDeleted())
                .collect(Collectors.toList());
        return users.stream().map(UserDto::fromEntity).toList();
    }

    @Transactional
    public UserDto updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.name() != null) {
            user.setName(request.name());
        }
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndDeletedFalse(request.email())) {
                throw new RuntimeException("Email is already in use");
            }
            user.setEmail(request.email());
        }
        if (request.roles() != null) {
            Set<RoleEntity> roles = roleRepository.findByNameIn(request.roles());
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return UserDto.fromEntity(updatedUser);
    }

    @Transactional
    public void deleteUser(String userId, String deletedBy) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeletedBy(deletedBy);
        userRepository.delete(user);
    }

    @Transactional
    public void restoreUser(String userId) {
        userRepository.restoreById(userId);
    }
}
