package tz.co.flex.payment.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.co.flex.payment.model.User;
import tz.co.flex.payment.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("User not found with username: " + username)
                );
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();
        return UserPrincipal.create(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), roleNames);
    }

    @Transactional
    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new UsernameNotFoundException("User not found with id: " + id)
        );
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();

        return UserPrincipal.create(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), roleNames);
    }
}
