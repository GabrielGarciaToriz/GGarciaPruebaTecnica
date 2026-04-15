package com.digis01.GGarciaPruebaTecnica.DTO;

import com.digis01.GGarciaPruebaTecnica.Model.Direccion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    @Pattern(
            regexp = "^(\\+?[0-9]{1,4}[\\s\\-])?[0-9\\s]{10,15}$",
            message = "El telefono debe de ener 10 digitos")
    private String phone;
    @NotBlank
    private String password;
    @NotBlank
    @Pattern(regexp = "^[A-Z&Ñ]{3,4}[0-9]{6}[A-Z0-9]{3}$",
            message = "Debe de tener un formato RFC valido")
    private String rfc;
    private List<Direccion> direcciones;
}
