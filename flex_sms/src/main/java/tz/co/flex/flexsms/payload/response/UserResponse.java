package tz.co.flex.flexsms.payload.response;

import lombok.Data;
import tz.co.flex.flexsms.model.ERole;

import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String name;
    private Set<ERole> roles;
    private boolean active;
}
