package tz.co.flex.payment.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tz.co.flex.payment.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    @Query("SELECT u FROM User u WHERE u.deleted = true")
    List<User> findAllDeleted();
    
    @Query("UPDATE User u SET u.deleted = false, u.deletedAt = NULL, u.deletedBy = NULL WHERE u.id = ?1")
    @Modifying
    @Transactional
    void restoreById(String id);
    
    Optional<User> findByIdAndDeletedFalse(String id);
    
    boolean existsByUsernameAndDeletedFalse(String username);
    
    boolean existsByEmailAndDeletedFalse(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByUsername(String username);
}
