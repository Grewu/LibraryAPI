package com.modsen.model.dto.response;

/**
 * DTO for a user response.
 *
 * <p>This class contains the details of a user account, including the user's unique identifier,
 * email address, and role ID.
 */
public record UserResponse(Long id, String email, Long role) {}
