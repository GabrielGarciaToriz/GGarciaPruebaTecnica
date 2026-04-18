package com.digis01.GGarciaPruebaTecnica.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    private UUID id;
    private String email;
    private String name;
    private String phone;
    private String password;
    private String rfc;
    private Date created_at;
    public List<Direccion> direcciones;

}
