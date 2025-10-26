package microopshub.userservice.service;

import microopshub.userservice.common.dto.UserLoginRequest;
import microopshub.userservice.common.dto.UserRegisterRequest;
import microopshub.userservice.common.dto.UserResponse;
import microopshub.userservice.common.exception.FunctionalException;
import microopshub.userservice.common.exception.UserAlreadyExistsException;
import microopshub.userservice.common.exception.UserNotFoundException;
import microopshub.userservice.entity.User;
import microopshub.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse registerUser(UserRegisterRequest userRequest) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(userRequest.email()).isPresent())
            throw new UserAlreadyExistsException("User with Given email ID is already registered.");

        User user = new User();
        user.setName(userRequest.name());
        user.setEmail(userRequest.email());
        user.setPassword(userRequest.password());
        User createdUser = userRepository.save(user);
        return new UserResponse(createdUser);
    }

    public UserResponse loginUser(UserLoginRequest loginRequest) throws UserNotFoundException {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (!user.getPassword().equals(loginRequest.password())) {
            throw new FunctionalException("Invalid email or password.");
        }
        return new UserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        return new UserResponse(user);
    }

    public UserResponse updateUser(Long id, UserRegisterRequest registerRequest) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        user.setName(registerRequest.name());
        user.setEmail(registerRequest.email());
        user.setPassword(registerRequest.password());
        return new UserResponse(userRepository.save(user));
    }
}
