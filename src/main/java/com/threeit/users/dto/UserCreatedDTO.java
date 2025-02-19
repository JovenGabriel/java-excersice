package com.threeit.users.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserCreatedDTO {

    private UUID id;
    private LocalDateTime createdAt;
    private String token;

}
