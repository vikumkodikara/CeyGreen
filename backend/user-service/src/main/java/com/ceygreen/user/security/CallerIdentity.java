package com.ceygreen.user.security;

import com.ceygreen.user.common.ApiException;
import com.ceygreen.user.users.Role;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Who the caller is, resolved either from a locally validated bearer token or from the
 * identity headers the gateway forwards after it validated the token.
 */
public record CallerIdentity(UUID userId, Role role) {

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    /** True when this caller owns the given user/farmer id, or is an admin. */
    public boolean canActAs(UUID subjectId) {
        return isAdmin() || (userId != null && userId.equals(subjectId));
    }

    public void requireCanActAs(UUID subjectId, String what) {
        if (!canActAs(subjectId)) {
            throw ApiException.forbidden(
                    "Authenticated user is not permitted to " + what + " for another account");
        }
    }

    public static CallerIdentity of(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw ApiException.unauthorized("Authentication is required");
        }
        if (authentication.getPrincipal() instanceof CallerIdentity identity) {
            return identity;
        }
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            return fromJwt(jwtAuthentication.getToken());
        }
        throw ApiException.unauthorized("Unsupported authentication type");
    }

    private static CallerIdentity fromJwt(Jwt jwt) {
        String subject = jwt.getSubject();
        String role = jwt.getClaimAsString(JwtIssuer.CLAIM_ROLE);
        if (subject == null || role == null) {
            throw ApiException.unauthorized("Access token is missing the subject or role claim");
        }
        try {
            return new CallerIdentity(UUID.fromString(subject), Role.valueOf(role));
        } catch (IllegalArgumentException ex) {
            throw ApiException.unauthorized("Access token carries an unreadable identity");
        }
    }
}
