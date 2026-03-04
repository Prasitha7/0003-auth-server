package ac.nsbm.authserver.service;

import ac.nsbm.authserver.domain.Role;
import ac.nsbm.authserver.domain.User;
import ac.nsbm.authserver.dto.UserDto;
import ac.nsbm.authserver.dto.UserRegistrationRequest;
import ac.nsbm.authserver.exception.BadRequestException;
import ac.nsbm.authserver.exception.ResourceNotFoundException;
import ac.nsbm.authserver.mapper.UserMapper;
import ac.nsbm.authserver.repository.RoleRepository;
import ac.nsbm.authserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserDto register(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        Role userRole = roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role missing"));

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .build();
        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);
        log.info("Registered user with id={} username={}", savedUser.getId(), savedUser.getUsername());
        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserDto> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userMapper.toDto(findUserById(id));
    }

    @Transactional
    public UserDto promoteToAdmin(Long userId) {
        User user = findUserById(userId);
        Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Admin role missing"));
        user.getRoles().add(adminRole);
        log.info("Promoted user id={} to admin", userId);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto disableUser(Long userId) {
        User user = findUserById(userId);
        user.setEnabled(false);
        log.info("Disabled user id={}", userId);
        return userMapper.toDto(user);
    }

    @Transactional
    public void recordSuccessfulLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setAccountNonLocked(true);
        });
    }

    @Transactional
    public void recordFailedLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountNonLocked(false);
                log.warn("User {} locked due to failed login attempts", username);
            }
        });
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
