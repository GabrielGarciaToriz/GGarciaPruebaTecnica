package com.digis01.GGarciaPruebaTecnica.DTO;

import com.digis01.GGarciaPruebaTecnica.Model.Direccion;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Datos del empleado")
public class UserResponse {

    private UUID id;
    private String email;
    private String name;
    private String phone;
    private String rfc;
    private Date created_at;
    private List<Direccion> direcciones;
}
