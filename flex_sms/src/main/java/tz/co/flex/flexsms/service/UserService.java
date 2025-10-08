package tz.co.flex.flexsms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.co.flex.flexsms.model.User;
import tz.co.flex.flexsms.payload.request.UserRequest;
import tz.co.flex.flexsms.payload.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    Page<UserResponse> getUsers(Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse createUser(UserRequest userRequest);
    UserResponse updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
    User getCurrentUser();
}
