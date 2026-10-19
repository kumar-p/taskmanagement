package taskmanagement.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

@Validated
@ConfigurationProperties("app.jwt")
public record JwtProperties(
        RSAPublicKey publicKey,
        RSAPrivateKey privateKey,
        @DefaultValue("false") boolean ephemeralKeys,
        @DefaultValue("8h") @NotNull Duration ttl,
        @DefaultValue("task-management-system") @NotBlank String issuer) {
}
