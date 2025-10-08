package tz.co.flex.payment.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tz.co.flex.payment.model.Role;
import tz.co.flex.payment.model.RoleEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Role> {
    
    @Query("SELECT r FROM RoleEntity r WHERE r.deleted = true")
    List<RoleEntity> findAllDeleted();
    
    @Query("UPDATE RoleEntity r SET r.deleted = false, r.deletedAt = NULL, r.deletedBy = NULL WHERE r.name = ?1")
    @Modifying
    @Transactional
    void restoreByName(Role name);
    
    Optional<RoleEntity> findByNameAndDeletedFalse(Role name);
    
    Set<RoleEntity> findByNameIn(Set<Role> names);
}
