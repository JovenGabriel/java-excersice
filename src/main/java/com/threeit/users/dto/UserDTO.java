package com.threeit.users.dto;

import com.threeit.users.entities.Phone;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDTO {

    private String name;
    private String email;
    private List<Phone> phones;
}
