package com.ceygreen.userdiagnosis.users;

import com.ceygreen.userdiagnosis.common.ApiException;
import com.ceygreen.userdiagnosis.security.CallerIdentity;
import com.ceygreen.userdiagnosis.security.JwtIssuer;
import com.ceygreen.userdiagnosis.users.dto.LoginRequest;
import com.ceygreen.userdiagnosis.users.dto.RegisterRequest;
import com.ceygreen.userdiagnosis.users.dto.TokenResponse;
import com.ceygreen.userdiagnosis.users.dto.UpdateProfileRequest;
import com.ceygreen.userdiagnosis.users.dto.UserProfileResponse;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtIssuer jwtIssuer;

    /**
     * A real hash of an unguessable value, matched against when the email is unknown so that
     * "no such account" and "wrong password" cost the same amount of work.
     */
    private final String decoyHash;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, JwtIssuer jwtIssuer) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtIssuer = jwtIssuer;
        this.decoyHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        if (!request.role().isSelfRegisterable()) {
            throw ApiException.badRequest("Only FARMER or BUYER accounts can be self-registered");
        }
        String email = normalizeEmail(request.email());
        if (repository.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflict("An account already exists for this email address");
        }

        User user = new User(
                request.name().trim(),
                email,
                passwordEncoder.encode(request.password()),
                request.role(),
                trimToNull(request.farmLocation()),
                trimToNull(request.contactInfo()));

        User saved = repository.save(user);
        log.info("Registered {} account {}", saved.getRole(), saved.getId());
        return UserProfileResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = repository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElse(null);

        boolean credentialsValid = passwordEncoder.matches(request.password(),
                user != null ? user.getPasswordHash() : decoyHash);

        if (user == null || !credentialsValid) {
            throw ApiException.unauthorized("Invalid email or password");
        }

        JwtIssuer.IssuedToken token = jwtIssuer.issue(user);
        log.info("Issued access token for {} account {}", user.getRole(), user.getId());
        return TokenResponse.bearer(token.value(), token.expiresInSeconds(), user.getId(), user.getRole());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(UUID id) {
        return repository.findById(id)
                .map(UserProfileResponse::from)
                .orElseThrow(() -> ApiException.notFound("No user exists with id " + id));
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID id, UpdateProfileRequest request, CallerIdentity caller) {
        caller.requireCanActAs(id, "update the profile");

        User user = repository.findById(id)
                .orElseThrow(() -> ApiException.notFound("No user exists with id " + id));

        if (request.name() != null) {
            user.setName(request.name().trim());
        }
        if (request.farmLocation() != null) {
            user.setFarmLocation(trimToNull(request.farmLocation()));
        }
        if (request.contactInfo() != null) {
            user.setContactInfo(trimToNull(request.contactInfo()));
        }

        return UserProfileResponse.from(repository.save(user));
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
