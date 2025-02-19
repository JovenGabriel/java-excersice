package com.threeit.users.serviceImpl;

import com.threeit.users.dto.UserCreateDTO;
import com.threeit.users.dto.UserCreatedDTO;
import com.threeit.users.dto.UserDTO;
import com.threeit.users.dto.UserLoginDTO;
import com.threeit.users.entities.Phone;
import com.threeit.users.entities.User;
import com.threeit.users.exceptions.EmailAlreadyExistException;
import com.threeit.users.exceptions.NotFoundException;
import com.threeit.users.repositories.PhoneRepository;
import com.threeit.users.repositories.UserRepository;
import com.threeit.users.service.UserService;
import com.threeit.users.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PhoneRepository phoneRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a list of UserDTO objects representing all users
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream().map(user -> UserDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phones(user.getPhones())
                .build()).toList();
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the UUID of the user to be retrieved
     * @return the User object associated with the specified UUID
     * @throws NotFoundException if no user is found with the given UUID
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }


    /**
     * Creates a new user in the system using the provided UserCreateDTO. This method verifies if
     * the email is already taken, encodes the user's password, and generates a JWT token for the user.
     *
     * @param userCreateDTO the data transfer object containing user information for creation
     * @return the newly created User entity
     * @throws EmailAlreadyExistException if a user with the specified email already exists
     */
    @Override
    @Transactional
    public UserCreatedDTO createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.findByEmail(userCreateDTO.getEmail()).isPresent())
            throw new EmailAlreadyExistException();

        List<Phone> phones = userCreateDTO.getPhones().stream().map((phoneDTO) -> Phone.builder()
                        .number(phoneDTO.getNumber())
                        .areaCode(phoneDTO.getAreaCode())
                        .countryCode(phoneDTO.getCountryCode())
                        .build())
                .toList();

        phoneRepository.saveAll(phones);

        User user = User.builder()
                .name(userCreateDTO.getName())
                .email(userCreateDTO.getEmail())
                .password(passwordEncoder.encode(userCreateDTO.getPassword()))
                .phones(phones)
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .token(jwtTokenUtil.generateToken(userCreateDTO.getEmail()))
                .build();
        User user1 = userRepository.save(user);

        return UserCreatedDTO.builder()
                .id(user1.getId())
                .createdAt(LocalDateTime.now())
                .token(user1.getToken())
                .build();
    }

    /**
     * Authenticates a user using their email and password.
     *
     * @param userLoginDTO the data transfer object containing the user's login credentials, including email and password
     * @return the authenticated User with an updated token and last login time
     * @throws NotFoundException if the email or password is incorrect
     */
    @Override
    @Transactional
    public User login(UserLoginDTO userLoginDTO) {
        User user = userRepository.findByEmail(userLoginDTO.getEmail())
                .orElseThrow(() -> new NotFoundException("Invalid email or password"));

        if (passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            String token = jwtTokenUtil.generateToken(user.getEmail());
            user.setToken(token);
            user.setLastLogin(LocalDateTime.now());
            return userRepository.save(user);
        } else {
            throw new NotFoundException("Invalid email or password");
        }
    }
}
