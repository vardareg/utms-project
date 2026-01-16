package com.iztech.utms.payload;

import com.iztech.utms.model.User.Role;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Email
    private String email;

    private Role role;

    private String userType;

    private Boolean enabled;

    private Integer facultyId;

    private Integer departmentId;
}
