package com.example.cuentas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDto {
    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String dni;
    private String telefono;
    private String estado;
}
