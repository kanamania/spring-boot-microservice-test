package tz.co.flex.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tz.co.flex.payment.dto.RoleDto;
import tz.co.flex.payment.model.Role;
import tz.co.flex.payment.security.UserPrincipal;
import tz.co.flex.payment.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleDto> getAllRoles(
        @RequestParam(required = false, defaultValue = "false") boolean includeDeleted
    ) {
        return roleService.getAllRoles(includeDeleted);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public RoleDto createRole(
        @RequestParam Role role,
        @RequestParam(required = false) String description,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return roleService.createRole(role, description);
    }

    @DeleteMapping("/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(
        @PathVariable Role role,
        @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        roleService.deleteRole(role, currentUser.getUsername());
    }

    @PostMapping("/{role}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreRole(@PathVariable Role role) {
        roleService.restoreRole(role);
    }
}
