package com.example.clientes.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clientes")
public class Cliente {

    @Id
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "El email debe ser valido")
    @NotBlank(message = "El email es obligatorio")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "El DNI es obligatorio")
    @Indexed(unique = true)
    private String dni;

    private String telefono;
    private String direccion;

    @Builder.Default
    private EstadoCliente estado = EstadoCliente.ACTIVO;

    @CreatedDate
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;

    public enum EstadoCliente {
        ACTIVO, INACTIVO
    }
}
