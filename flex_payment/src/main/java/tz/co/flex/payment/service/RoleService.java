package tz.co.flex.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tz.co.flex.payment.dto.RoleDto;
import tz.co.flex.payment.model.Role;
import tz.co.flex.payment.model.RoleEntity;
import tz.co.flex.payment.repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @Transactional
    public List<RoleDto> getAllRoles(boolean includeDeleted) {
        List<RoleEntity> roles = includeDeleted ? 
            roleRepository.findAll() : 
            roleRepository.findAll().stream()
                .filter(role -> !role.isDeleted())
                .toList();
        return roles.stream().map(RoleDto::fromEntity).toList();
    }

    @Transactional
    public RoleDto createRole(Role role, String description) {
        if (roleRepository.existsById(role)) {
            throw new RuntimeException("Role already exists");
        }

        RoleEntity roleEntity = new RoleEntity(role);
        RoleEntity savedRole = roleRepository.save(roleEntity);
        return RoleDto.fromEntity(savedRole);
    }

    @Transactional
    public void deleteRole(Role role, String deletedBy) {
        RoleEntity roleEntity = roleRepository.findById(role)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        roleEntity.setDeletedBy(deletedBy);
        roleRepository.delete(roleEntity);
    }

    @Transactional
    public void restoreRole(Role role) {
        roleRepository.restoreByName(role);
    }
}
