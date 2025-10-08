package tz.co.flex.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.co.flex.payment.model.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByApiToken(String apiToken);
    boolean existsByApiToken(String apiToken);

    Optional<Account> findByName(String name);
}
