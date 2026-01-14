package com.iztech.utms.payload;

import com.iztech.utms.model.User.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private String userType;
    private boolean enabled;
}
