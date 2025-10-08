package tz.co.flex.payment.config;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import tz.co.flex.payment.model.Account;
import tz.co.flex.payment.model.Role;
import tz.co.flex.payment.model.RoleEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tz.co.flex.payment.model.User;
import tz.co.flex.payment.repository.AccountRepository;
import tz.co.flex.payment.repository.RoleRepository;
import tz.co.flex.payment.repository.UserRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminUserInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @PostConstruct
    @Transactional
    public void init() {
        // Create roles if they don't exist
        for (Role role : Role.values()) {
            roleRepository.findById(role).orElseGet(() -> {
                log.info("Creating role: {}", role);
                return roleRepository.save(new RoleEntity(role));
            });
        }

        // Create admin user if it doesn't exist
        String adminEmail = "admin@flex.co.tz";
        if (!userRepository.existsByEmailAndDeletedFalse(adminEmail)) {
            log.info("Creating admin user: {}", adminEmail);
            User admin = new User(
                "System Administrator",
                "admin",
                adminEmail,
                passwordEncoder.encode("Admin@123")
            );
            
            // Convert RoleEntity to Role using ModelMapper
            Set<RoleEntity> roles = new HashSet<>(roleRepository.findAll());
            admin.setRoles(roles);
            userRepository.save(admin);
            log.info("Admin user created successfully");
        }

        // Create test accounts if they don't exist
        createTestAccount("Test Account 1", "API-83c27eb209652dbdd1d8a725f762ee018deea8fd285ed3a2736260b524293522");
        createTestAccount("Test Account 2", "API-TEST-ACCOUNT-2");
        createTestAccount("Test Account 3", "API-TEST-ACCOUNT-3");
    }

    private void createTestAccount(String name, String apiToken) {
        if (!accountRepository.existsByApiToken(apiToken)) {
            log.info("Creating test account: {}", name);
            Account account = new Account();
            account.setName(name);
            account.setApiToken(apiToken);
            account.setActive(true);
            account.setWebhookUrl("http://localhost:8081/payments/status");
            account.setAllowedIps("127.0.0.1,::1");
            accountRepository.save(account);
        }
    }
}
