package com.ceygreen.user.users;

import com.ceygreen.user.common.ApiError;
import com.ceygreen.user.security.CallerIdentity;
import com.ceygreen.user.users.dto.LoginRequest;
import com.ceygreen.user.users.dto.RegisterRequest;
import com.ceygreen.user.users.dto.TokenResponse;
import com.ceygreen.user.users.dto.UpdateProfileRequest;
import com.ceygreen.user.users.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "Account registration, login and profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a farmer or buyer account",
            description = "Public endpoint. The password is stored as a bcrypt hash and is never returned.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(responseCode = "400", description = "Validation failed, or role is not FARMER/BUYER",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Email already registered",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public UserProfileResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Verify credentials and issue an OAuth 2.0 access token",
            description = "Public endpoint. The returned bearer token carries the caller's userId, "
                    + "role and farmerId/buyerId as claims, and is never persisted server-side.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token issued"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "apiKey")
    @Operation(summary = "Retrieve a user's public profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token, or missing API key",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "No such user",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public UserProfileResponse getProfile(@PathVariable UUID id) {
        return userService.getProfile(id);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @SecurityRequirement(name = "apiKey")
    @Operation(summary = "Update profile details",
            description = "A user may only update their own profile; an admin may update any. "
                    + "Email, role and password are not editable here.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "403", description = "Attempted to update another account's profile",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "No such user",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public UserProfileResponse updateProfile(@PathVariable UUID id,
                                            @Valid @RequestBody UpdateProfileRequest request,
                                            Authentication authentication) {
        return userService.updateProfile(id, request, CallerIdentity.of(authentication));
    }
}
