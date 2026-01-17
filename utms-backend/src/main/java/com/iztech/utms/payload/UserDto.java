package com.iztech.utms.payload;

import com.iztech.utms.model.User.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String userType;
    private boolean enabled;
    private Integer facultyId;
    private String facultyName;
    private Integer departmentId;
    private String departmentName;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
