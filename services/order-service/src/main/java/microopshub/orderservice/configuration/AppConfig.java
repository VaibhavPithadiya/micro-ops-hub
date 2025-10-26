package microopshub.orderservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties()
public record AppConfig(UserService userService, String corsOrigin) {
    public record UserService(String url) {
    }
}
