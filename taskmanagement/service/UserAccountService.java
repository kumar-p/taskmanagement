package taskmanagement.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import taskmanagement.dto.RegisterRequest;
import taskmanagement.entity.UserAccount;
import taskmanagement.exception.UserAlreadyExistsException;
import taskmanagement.repository.UserAccountRepository;

import java.util.Locale;

@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder bCryptPasswordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        String normalizedEmail = registerRequest.email().trim().toLowerCase(Locale.ROOT);
        if (userAccountRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new UserAlreadyExistsException("An account with this email already exists.");
        }

        // Hash plain text password
        String encodedPassword = bCryptPasswordEncoder.encode(registerRequest.password());

        UserAccount userAccount = new UserAccount(normalizedEmail, encodedPassword);
        userAccountRepository.save(userAccount);
    }
}
