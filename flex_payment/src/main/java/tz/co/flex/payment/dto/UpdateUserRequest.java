package tz.co.flex.payment.dto;

import jakarta.validation.constraints.Email;
import tz.co.flex.payment.model.Role;

import java.util.Set;

public record UpdateUserRequest(
    String name,
    @Email String email,
    Set<Role> roles
) {}
