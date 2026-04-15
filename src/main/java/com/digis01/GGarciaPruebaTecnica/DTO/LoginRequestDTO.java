package com.digis01.GGarciaPruebaTecnica.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    private String rfc;
    @NotBlank
    private String password;
}
