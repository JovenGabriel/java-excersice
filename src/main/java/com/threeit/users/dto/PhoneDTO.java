package com.threeit.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhoneDTO {

    @NotBlank
    private String number;
    @NotBlank
    private String areaCode;
    @NotBlank
    private String countryCode;
}
