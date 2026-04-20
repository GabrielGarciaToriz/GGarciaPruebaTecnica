
package com.digis01.GGarciaPruebaTecnica.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String message;
    private String taxId;
    private String token;
}