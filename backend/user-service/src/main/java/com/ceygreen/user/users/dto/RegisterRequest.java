package com.ceygreen.user.users.dto;

import com.ceygreen.user.users.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        @Size(min = 2, max = 120)
        String name,

        @NotBlank
        @Email
        @Size(max = 190)
        String email,

        @NotBlank
        @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters")
        String password,

        @NotNull(message = "role must be FARMER or BUYER")
        Role role,

        @Size(max = 200)
        String farmLocation,

        @Size(max = 120)
        String contactInfo) {
}
