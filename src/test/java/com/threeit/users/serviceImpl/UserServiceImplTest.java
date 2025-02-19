package com.threeit.users.serviceImpl;

import com.threeit.users.dto.PhoneDTO;
import com.threeit.users.dto.UserCreateDTO;
import com.threeit.users.dto.UserCreatedDTO;
import com.threeit.users.dto.UserLoginDTO;
import com.threeit.users.entities.User;
import com.threeit.users.exceptions.EmailAlreadyExistException;
import com.threeit.users.exceptions.NotFoundException;
import com.threeit.users.repositories.UserRepository;
import com.threeit.users.utils.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserServiceImpl class. This test class uses Mockito for mocking dependencies
 * and SpringBootTest for integration with the Spring TestContext framework. The tests are designed
 * to validate the functionality of creating users, handling scenarios such as email already exists,
 * and user login functionalities.
 * <p>
 * Mocks:
 * - UserRepository: Mocked to simulate user persistence interactions.
 * - PhoneRepository: Mocked to simulate phone persistence interactions.
 * - BCryptPasswordEncoder: Mocked to simulate password encoding.
 * - JwtTokenUtil: Mocked to simulate JWT token operations.
 * <p>
 * Injects:
 * - UserServiceImpl: The service being tested, which has its dependencies injected as mocks.
 * <p>
 * Methods tested:
 * - testCreateUserSuccessfully: Tests the successful creation of a user ensuring all attributes are properly set.
 * - testCreateUserThrowsEmailAlreadyExistsException: Tests that creating a user with an existing email throws the correct exception.
 * - testLoginSuccessfully: Verifies that a user can log in successfully and receive a valid token.
 * - testLoginWithInvalidEmailThrowsNotFoundException: Confirms that attempting to log in with an invalid email will throw a NotFoundException.
 * - testLoginWithInvalidPasswordThrowsNotFoundException: Checks that logging in with a valid email but invalid password throws a NotFoundException.
 * <p>
 * The test methods use assertions to confirm the expected behavior, including successful operations and exception handling.
 */
@SpringBootTest
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserServiceImpl userServiceImpl;

    public UserServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test successful user creation")
    void testCreateUserSuccessfully() {
        // Arrange
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("John Doe");
        userCreateDTO.setEmail("john.doe@example.com");
        userCreateDTO.setPassword("Password123!");

        List<PhoneDTO> phones = new ArrayList<>();
        phones.add(new PhoneDTO("123456789", "123", "1"));
        userCreateDTO.setPhones(phones);

        when(userRepository.findByEmail(userCreateDTO.getEmail())).thenReturn(Optional.empty());
        when(jwtTokenUtil.generateToken(userCreateDTO.getEmail())).thenReturn("token");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        UserCreatedDTO createdUser = userServiceImpl.createUser(userCreateDTO);

        // Assert
        assertNotNull(createdUser);
        assertNotNull(createdUser.getCreatedAt());
        assertNotNull(createdUser.getToken());
    }

    @Test
    @DisplayName("Test throwing exception when email already exists during user creation")
    void testCreateUserThrowsEmailAlreadyExistsException() {
        // Arrange
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("John Doe");
        userCreateDTO.setEmail("john@example.com");
        userCreateDTO.setPassword("Password123!");

        List<PhoneDTO> phones = new ArrayList<>();
        phones.add(new PhoneDTO("123456789", "123", "1"));
        userCreateDTO.setPhones(phones);

        userServiceImpl.createUser(userCreateDTO);

        UserCreateDTO userCreateDTO2 = new UserCreateDTO();
        userCreateDTO2.setName("John Doe");
        userCreateDTO2.setEmail("john@example.com");
        userCreateDTO2.setPassword("Password123!");

        List<PhoneDTO> phones2 = new ArrayList<>();
        phones2.add(new PhoneDTO("987654321", "123", "1"));
        userCreateDTO2.setPhones(phones2);


        // Act & Assert
        assertThrows(EmailAlreadyExistException.class, () -> userServiceImpl.createUser(userCreateDTO2));
    }

    @Test
    @DisplayName("Test successful user login")
    void testLoginSuccessfully() {
        // Arrange
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("John Doe");
        userCreateDTO.setEmail("john.doe@example.cl");
        userCreateDTO.setPassword("Password123!");

        List<PhoneDTO> phones = new ArrayList<>();
        phones.add(new PhoneDTO("123456789", "123", "1"));
        userCreateDTO.setPhones(phones);

        userServiceImpl.createUser(userCreateDTO);

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("john.doe@example.cl");
        userLoginDTO.setPassword("Password123!");

        // Act
        User loggedInUser = userServiceImpl.login(userLoginDTO);

        // Assert
        assertNotNull(loggedInUser);
        assertNotNull(loggedInUser.getToken());
    }

    @Test
    @DisplayName("Test throwing exception for login attempt with invalid email")
    void testLoginWithInvalidEmailThrowsNotFoundException() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("invalid@example.com");
        userLoginDTO.setPassword("Password123!");

        when(userRepository.findByEmail(userLoginDTO.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userServiceImpl.login(userLoginDTO));
    }

    @Test
    @DisplayName("Test throwing exception for login attempt with invalid password")
    void testLoginWithInvalidPasswordThrowsNotFoundException() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("john.doe@example.com");
        userLoginDTO.setPassword("WrongPassword!");

        User user = User.builder()
                .email("john.doe@example.com")
                .password(passwordEncoder.encode("Password123!"))
                .build();

        when(userRepository.findByEmail(userLoginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userServiceImpl.login(userLoginDTO));
    }

    @Test
    @DisplayName("Test throwing exception when user not found by ID")
    void testGetUserByIdThrowsNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userServiceImpl.getUserById(userId));
    }
}