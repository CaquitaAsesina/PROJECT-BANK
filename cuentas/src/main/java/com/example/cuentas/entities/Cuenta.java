package com.example.cuentas.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cuentas")
public class Cuenta {
    @Id
    private String id;

    @NotBlank(message = "El numero de cuenta es obligatorio")
    @Indexed(unique = true)
    private String numeroCuenta;

    @NotBlank(message = "El ID del cliente es obligatorio")
    private String clienteId;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private TipoCuenta tipoCuenta;

    @PositiveOrZero(message = "El saldo no puede ser negativo")
    @Builder.Default
    private BigDecimal salgo = BigDecimal.ZERO;

    private String moneda;

    @Builder.Default
    private EstadoCuenta estado = EstadoCuenta.ACTIVA;

    @CreatedDate
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;

    public enum TipoCuenta {
        AHORROS, CORRIENTE
    }

    public enum EstadoCuenta {
        ACTIVA, INACTIVA, BLOQUEADA
    }
}
