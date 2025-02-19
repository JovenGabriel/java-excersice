package com.threeit.users.dto;

import com.threeit.users.annotations.ValidPassword;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class UserCreateDTO {

    @NotBlank
    private String name;
    @NotBlank
    @Email
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email")
    private String email;
    @NotBlank
    @ValidPassword
    private String password;
    @NotEmpty
    @Valid
    private List<PhoneDTO> phones;

}
