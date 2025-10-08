package tz.co.flex.flexsms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.co.flex.flexsms.model.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find a customer by their customer ID
     */
    Optional<Customer> findByCustomerId(String customerId);

    // Add custom query methods here if needed
}
