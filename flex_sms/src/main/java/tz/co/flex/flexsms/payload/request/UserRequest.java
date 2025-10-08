package tz.co.flex.flexsms.payload.request;

import lombok.Data;
import tz.co.flex.flexsms.model.ERole;

import jakarta.validation.constraints.*;
import java.util.Set;

@Data
public class UserRequest {
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    private String name;

    private Set<ERole> roles;
    private boolean active = true;
}
