package taskmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanagement.dto.TokenResponse;
import taskmanagement.service.TokenService;

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final TokenService tokenService;

    AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(Authentication authentication) {
        return ResponseEntity.ok(new TokenResponse(tokenService.generateToken(authentication.getName())));
    }
}
