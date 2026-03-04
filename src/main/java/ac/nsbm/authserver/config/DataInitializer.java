package ac.nsbm.authserver.config;

import ac.nsbm.authserver.domain.Role;
import ac.nsbm.authserver.repository.RoleRepository;
import ac.nsbm.authserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;

    @Bean
    CommandLineRunner seedRoles() {
        return args -> {
            if (roleRepository.findByName(UserService.ROLE_USER).isEmpty()) {
                roleRepository.save(Role.builder().name(UserService.ROLE_USER).build());
            }
            if (roleRepository.findByName(UserService.ROLE_ADMIN).isEmpty()) {
                roleRepository.save(Role.builder().name(UserService.ROLE_ADMIN).build());
            }
        };
    }
}
