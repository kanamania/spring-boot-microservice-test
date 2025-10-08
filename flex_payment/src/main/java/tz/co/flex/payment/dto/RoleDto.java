package tz.co.flex.payment.dto;

import tz.co.flex.payment.model.RoleEntity;

import java.time.LocalDateTime;

public record RoleDto(
    String name
) {
    public static RoleDto fromEntity(RoleEntity role) {
        return new RoleDto(role.getName().name());
    }
}
