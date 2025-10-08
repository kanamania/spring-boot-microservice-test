package tz.co.flex.flexsms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tz.co.flex.flexsms.model.ERole;
import tz.co.flex.flexsms.model.Role;
import tz.co.flex.flexsms.model.User;
import tz.co.flex.flexsms.repository.RoleRepository;
import tz.co.flex.flexsms.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);

            Role staffRole = new Role();
            staffRole.setName(ERole.ROLE_STAFF);

            Role customerRole = new Role();
            customerRole.setName(ERole.ROLE_CUSTOMER);

            roleRepository.save(adminRole);
            roleRepository.save(staffRole);
            roleRepository.save(customerRole);
        }

        // Initialize admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@flex.co.tz");
            admin.setFullName("System Administrator");
            admin.setPassword(passwordEncoder.encode("admin123"));

            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Admin role not found."));
            roles.add(adminRole);
            Role staffRole = roleRepository.findByName(ERole.ROLE_STAFF)
                    .orElseThrow(() -> new RuntimeException("Error: Staff role not found."));
            roles.add(staffRole);
            admin.setRoles(roles);

            userRepository.save(admin);
        }
    }
}
