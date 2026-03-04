package ac.nsbm.authserver.dto;

import java.time.Instant;
import java.util.Set;

public record UserDto(
        Long id,
        String username,
        String email,
        boolean enabled,
        boolean accountNonLocked,
        int failedLoginAttempts,
        Set<String> roles,
        Instant createdAt,
        Instant updatedAt
) {
}
