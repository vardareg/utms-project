package com.iztech.utms.payload;

import com.iztech.utms.model.User.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and include uppercase, lowercase, numbers, and special characters.")
    private String password;

    @NotNull
    private Role role;

    @NotBlank
    private String userType;

    // Optional: For Administrative Profiles (Dean/YGK)
    private Integer facultyId;
    private Integer departmentId;
}
