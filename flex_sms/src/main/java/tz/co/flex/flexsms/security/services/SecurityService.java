package tz.co.flex.flexsms.security.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityService {
    String findLoggedInUsername();
    Authentication getAuthentication();
    UserDetails getAuthenticatedUser();
    boolean isAuthenticated();
}
