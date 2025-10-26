package microopshub.userservice.common.dto;

import microopshub.userservice.entity.User;

public record UserResponse(Long id, String name, String email, String role) implements ApiResponse {
    public UserResponse(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
