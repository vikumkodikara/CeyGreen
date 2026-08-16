package com.ceygreen.user.users.dto;

import com.ceygreen.user.users.Role;
import com.ceygreen.user.users.User;
import java.time.Instant;
import java.util.UUID;

/**
 * The only shape in which a user is ever returned. There is no field for the password hash,
 * so it cannot leak through this endpoint by accident.
 */
public record UserProfileResponse(
        UUID id,
        String name,
        String email,
        Role role,
        String farmLocation,
        String contactInfo,
        Instant createdAt) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getFarmLocation(),
                user.getContactInfo(),
                user.getCreatedAt());
    }
}
