package com.iztech.utms.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and include uppercase, lowercase, numbers, and special characters.")
    private String password;

    @NotBlank(message = "TCKN is required")
    @Size(min = 11, max = 11, message = "TCKN must be exactly 11 characters")
    @Pattern(regexp = "^[0-9]+$", message = "TCKN must contain only numbers")
    private String tckn;
}
