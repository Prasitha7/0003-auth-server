package ac.nsbm.authserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tokens")
@Tag(name = "Token Management")
public class TokenController {

    private final OAuth2AuthorizationService authorizationService;

    public TokenController(OAuth2AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping("/revoke")
    @Operation(summary = "Revoke refresh token (logout)")
    public void revoke(@RequestParam("refresh_token") String refreshToken) {
        OAuth2Authorization authorization = authorizationService.findByToken(
                refreshToken, org.springframework.security.oauth2.server.authorization.OAuth2TokenType.REFRESH_TOKEN);
        if (authorization != null) {
            authorizationService.remove(authorization);
        }
    }
}
