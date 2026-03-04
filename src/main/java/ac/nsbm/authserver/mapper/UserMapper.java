package ac.nsbm.authserver.mapper;

import ac.nsbm.authserver.domain.User;
import ac.nsbm.authserver.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.isAccountNonLocked(),
                user.getFailedLoginAttempts(),
                user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
