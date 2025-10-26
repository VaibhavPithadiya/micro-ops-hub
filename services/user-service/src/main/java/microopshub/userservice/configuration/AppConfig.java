package microopshub.userservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties()
public record AppConfig(String corsOrigin) {
}
