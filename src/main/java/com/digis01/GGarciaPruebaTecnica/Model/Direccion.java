package com.digis01.GGarciaPruebaTecnica.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {

    private int id;
    private String name;
    private String street;
    private String country_code;

}
