package com.threeit.users.service;

import com.threeit.users.dto.UserCreateDTO;
import com.threeit.users.dto.UserCreatedDTO;
import com.threeit.users.dto.UserDTO;
import com.threeit.users.dto.UserLoginDTO;
import com.threeit.users.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserDTO> getUsers();
    User getUserById(UUID id);
    UserCreatedDTO createUser(UserCreateDTO userCreateDTO);
    User login(UserLoginDTO userLoginDTO);
}
