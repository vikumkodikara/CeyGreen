package com.ceygreen.userdiagnosis.users.dto;

import jakarta.validation.constraints.Size;

/**
 * Every field is optional; a null field leaves the stored value untouched. Email, role and
 * password are deliberately not updatable here -- email and role are identity, and password
 * changes belong behind their own credential-confirming flow.
 */
public record UpdateProfileRequest(

        @Size(min = 2, max = 120)
        String name,

        @Size(max = 200)
        String farmLocation,

        @Size(max = 120)
        String contactInfo) {
}
