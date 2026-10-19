package taskmanagement.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanagement.dto.RegisterRequest;
import taskmanagement.service.UserAccountService;

@RestController
@RequestMapping("/api/accounts")
class AccountController {

    private final UserAccountService userAccountService;

    AccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        userAccountService.registerUser(registerRequest);
        return ResponseEntity.ok("User account created successfully");
    }
}
