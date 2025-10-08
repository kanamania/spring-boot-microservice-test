package tz.co.flex.payment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tz.co.flex.payment.model.Role;

import java.util.Set;

public record CreateUserRequest(
    @NotBlank String name,
    @NotBlank String username,
    @Email String email,
    @Size(min = 6) String password,
    Set<Role> roles
) {}
