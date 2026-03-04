package ac.nsbm.authserver.service;

import ac.nsbm.authserver.domain.Role;
import ac.nsbm.authserver.domain.User;
import ac.nsbm.authserver.dto.UserRegistrationRequest;
import ac.nsbm.authserver.exception.BadRequestException;
import ac.nsbm.authserver.mapper.UserMapper;
import ac.nsbm.authserver.repository.RoleRepository;
import ac.nsbm.authserver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private final UserMapper userMapper = new UserMapper();

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, roleRepository, passwordEncoder, userMapper);
    }

    @Test
    void shouldRegisterUserWithRoleUser() {
        UserRegistrationRequest request = new UserRegistrationRequest("johndoe", "john@acme.com", "abc12345");
        Role role = Role.builder().id(1L).name(UserService.ROLE_USER).build();

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(roleRepository.findByName(UserService.ROLE_USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        var created = userService.register(request);
        assertTrue(created.roles().contains(UserService.ROLE_USER));
    }

    @Test
    void shouldRejectDuplicateUsername() {
        UserRegistrationRequest request = new UserRegistrationRequest("johndoe", "john@acme.com", "abc12345");
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.register(request));
    }
}
