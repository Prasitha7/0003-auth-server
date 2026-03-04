package ac.nsbm.authserver.controller;

import ac.nsbm.authserver.dto.UserDto;
import ac.nsbm.authserver.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin Users")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List users with pagination")
    public Page<UserDto> listUsers(@PageableDefault(size = 20) Pageable pageable) {
        return userService.listUsers(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    public UserDto getById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}/promote")
    @Operation(summary = "Promote user to admin")
    public UserDto promote(@PathVariable Long id) {
        return userService.promoteToAdmin(id);
    }

    @PatchMapping("/{id}/disable")
    @Operation(summary = "Disable user")
    public UserDto disable(@PathVariable Long id) {
        return userService.disableUser(id);
    }
}
