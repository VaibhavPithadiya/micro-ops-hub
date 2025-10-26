package microopshub.userservice.service;

import microopshub.userservice.common.dto.UserLoginRequest;
import microopshub.userservice.common.dto.UserRegisterRequest;
import microopshub.userservice.common.dto.UserResponse;
import microopshub.userservice.common.exception.FunctionalException;
import microopshub.userservice.common.exception.UserAlreadyExistsException;
import microopshub.userservice.common.exception.UserNotFoundException;
import microopshub.userservice.entity.User;
import microopshub.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserRegisterRequest userRegisterRequest;
    private UserLoginRequest userLoginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRegisterRequest = new UserRegisterRequest("John Doe", "john.doe@example.com", "password123");

        userLoginRequest = new UserLoginRequest("john.doe@example.com", "password123");
    }

    @Test
    void testRegisterUserSuccess() throws UserAlreadyExistsException {
        // Arrange
        User user = new User();
        user.setName(userRegisterRequest.name());
        user.setEmail(userRegisterRequest.email());
        user.setPassword(userRegisterRequest.password());

        when(userRepository.findByEmail(userRegisterRequest.email())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse userResponse = userService.registerUser(userRegisterRequest);

        // Assert
        assertNotNull(userResponse);
        assertEquals(user.getName(), userResponse.name());
        assertEquals(user.getEmail(), userResponse.email());

        verify(userRepository, times(1)).findByEmail(userRegisterRequest.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserThrowsUserAlreadyExistsException() throws UserAlreadyExistsException {
        // Arrange
        when(userRepository.findByEmail(userRegisterRequest.email())).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(userRegisterRequest));

        verify(userRepository, times(1)).findByEmail(userRegisterRequest.email());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testLoginUserSuccess() throws UserNotFoundException {
        // Arrange
        User user = new User();
        user.setEmail(userLoginRequest.email());
        user.setPassword(userLoginRequest.password());

        when(userRepository.findByEmail(userLoginRequest.email())).thenReturn(Optional.of(user));

        // Act
        UserResponse userResponse = userService.loginUser(userLoginRequest);

        // Assert
        assertNotNull(userResponse);
        assertEquals(user.getEmail(), userResponse.email());

        verify(userRepository, times(1)).findByEmail(userLoginRequest.email());
    }

    @Test
    void testLoginUserThrowsUserNotFoundException() {
        // Arrange
        when(userRepository.findByEmail(userLoginRequest.email())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.loginUser(userLoginRequest));

        verify(userRepository, times(1)).findByEmail(userLoginRequest.email());
    }

    @Test
    void testLoginUserThrowsFunctionalException() {
        // Arrange
        User user = new User();
        user.setEmail(userLoginRequest.email());
        user.setPassword("wrongpassword");

        when(userRepository.findByEmail(userLoginRequest.email())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(FunctionalException.class, () -> userService.loginUser(userLoginRequest));

        verify(userRepository, times(1)).findByEmail(userLoginRequest.email());
    }

    @Test
    void testGetUserByIdSuccess() throws UserNotFoundException {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("john.doe@example.com");
        user.setName("John Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserResponse userResponse = userService.getUserById(userId);

        // Assert
        assertNotNull(userResponse);
        assertEquals(user.getId(), userResponse.id());
        assertEquals(user.getName(), userResponse.name());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserByIdThrowsUserNotFoundException() throws UserNotFoundException {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUserSuccess() throws UserNotFoundException {
        // Arrange
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("john.doe@example.com");
        existingUser.setName("John Doe");
        existingUser.setPassword("password123");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        UserResponse userResponse = userService.updateUser(userId, userRegisterRequest);

        // Assert
        assertNotNull(userResponse);
        assertEquals(existingUser.getId(), userResponse.id());
        assertEquals(existingUser.getName(), userResponse.name());
        assertEquals(existingUser.getEmail(), userResponse.email());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserThrowsUserNotFoundException() throws UserNotFoundException {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, userRegisterRequest));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(any(User.class));
    }
}