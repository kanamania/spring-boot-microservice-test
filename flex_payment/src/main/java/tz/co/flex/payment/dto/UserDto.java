package tz.co.flex.payment.dto;

import tz.co.flex.payment.model.Role;
import tz.co.flex.payment.model.RoleEntity;
import tz.co.flex.payment.model.User;

import java.util.Set;

public record UserDto(
    String id,
    String name,
    String username,
    String email,
    Set<RoleEntity> roles
) {
    public static UserDto fromEntity(User user) {
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles()
        );
    }
}
