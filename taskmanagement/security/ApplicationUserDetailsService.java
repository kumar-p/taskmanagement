package taskmanagement.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import taskmanagement.entity.UserAccount;
import taskmanagement.repository.UserAccountRepository;

@Service
class ApplicationUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    ApplicationUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount user = userAccountRepository
                .findByEmailIgnoreCase(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
