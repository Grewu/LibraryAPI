package com.modsen.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a user request.
 *
 * <p>This class holds essential information for creating a new user, including their email,
 * password, and role ID.
 */
public record UserRequest(
    @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a valid email address")
        String email,
    @NotBlank(message = "Password must not be blank")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password,
    @NotNull(message = "Role ID must not be null") Long role) {}
