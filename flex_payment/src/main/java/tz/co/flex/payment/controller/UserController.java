package tz.co.flex.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tz.co.flex.payment.dto.CreateUserRequest;
import tz.co.flex.payment.dto.UpdateUserRequest;
import tz.co.flex.payment.dto.UserDto;
import tz.co.flex.payment.security.UserPrincipal;
import tz.co.flex.payment.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAllUsers(
        @RequestParam(required = false, defaultValue = "false") boolean includeDeleted
    ) {
        return userService.getAllUsers(includeDeleted);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUser(
        @PathVariable String userId,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
        @PathVariable String userId,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        userService.deleteUser(userId, currentUser.getUsername());
    }

    @PostMapping("/{userId}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreUser(@PathVariable String userId) {
        userService.restoreUser(userId);
    }
}
