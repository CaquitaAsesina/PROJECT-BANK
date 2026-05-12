package com.example.cuentas.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaRequestDTO {
    private String clienteId;
    private String tipoCuenta;
    private String estado;
    private BigDecimal saldoInicial;

}
