package com.example.cuentas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.cuentas.dto.ClienteDto;
import com.example.cuentas.dto.CuentaRequestDTO;
import com.example.cuentas.entities.Cuenta;
import com.example.cuentas.entities.Cuenta.EstadoCuenta;
import com.example.cuentas.entities.Cuenta.TipoCuenta;
import com.example.cuentas.repository.CuentaRespository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRespository cuentaRespository;
    private final WebClient clientesWebClient;

    public Flux<Cuenta> listarCuentas() {
        log.info("Listando todas las cuentas");
        return cuentaRespository.findAll()
                .switchIfEmpty(Flux.error(new RuntimeException("Lista de cuentas vacia")))
                .doOnNext(c -> log.debug("Cuentas listados: {}", c.getId()))
                .doOnComplete(() -> log.info("Listado de cuentas completado"))
                .doOnError(e -> log.error("Error al listar cuentas: {}", e.getMessage()));
    }

    public Flux<Cuenta> buscarPorTipoCuenta(Cuenta.TipoCuenta tipo) {
        log.debug("Listando cuentas " + tipo);
        return cuentaRespository.findByTipoCuenta(tipo)
                .switchIfEmpty(Flux.error(new RuntimeException("Lista de cuentas " + tipo + "vacia")))
                .doOnNext(c -> log.debug("Cuentas listadas: {}", c.getId()))
                .doOnComplete(() -> log.info("Listado de cuentas " + tipo + "completado"))
                .doOnError(e -> log.error("Error al listar cuentas " + tipo + ":{}", e.getMessage()));
    }

    public Mono<Cuenta> buscarPorNumeroCuenta(String numeroCuenta) {
        log.debug("Buscando por numero de cuenta: {}", numeroCuenta);
        return Mono.justOrEmpty(numeroCuenta)
                .filter(i -> !i.isBlank())
                .switchIfEmpty(Mono.error(new RuntimeException("Numero de cuenta invalido")))
                .flatMap(CuentaNumero -> {
                    return cuentaRespository.findByNumeroCuenta(numeroCuenta);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Cuenta no encontrado: " + numeroCuenta)))
                .doOnSuccess(c -> log.info("Cuenta encontrada", c.getNumeroCuenta()))
                .doOnError(e -> log.error("Error buscando por numero de cuenta", e.getMessage()));
    }

    public Mono<Cuenta> crearCuenta(CuentaRequestDTO cuentaRequestDTO) {
        log.info("Iniciando creación de cuenta para clienteId: {}", cuentaRequestDTO.getClienteId());
        return clientesWebClient
                .get()
                .uri("/api/clientes/id/{id}", cuentaRequestDTO.getClienteId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.warn("Error 4xx al consultar cliente {}: {}",
                                            cuentaRequestDTO.getClienteId(), errorBody);
                                    return Mono.error(
                                            new RuntimeException(
                                                    "Cliente no encontrado con ID: "
                                                            + cuentaRequestDTO.getClienteId()));
                                }))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(
                                new RuntimeException(
                                        "Error en clientes-service. Intente más tarde.")))
                .bodyToMono(ClienteDto.class)
                .switchIfEmpty(
                        Mono.error(new RuntimeException(
                                "No se encontró ningún cliente con ID: "
                                        + cuentaRequestDTO.getClienteId())))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error HTTP al llamar a clientes-service: {} {}",
                            ex.getStatusCode(), ex.getMessage());
                    return Mono.error(new RuntimeException(
                            "Error al comunicarse con clientes-service: " + ex.getMessage()));
                })
                .flatMap(cliente -> {

                    log.info("Cliente encontrado: {} (Estado: {})",
                            cliente.getNombre(), cliente.getEstado());
                    if (!"ACTIVO".equals(cliente.getEstado())) {
                        log.warn("Intento de crear cuenta para cliente INACTIVO: {}",
                                cliente.getId());
                        return Mono.error(new RuntimeException(
                                "No se puede crear cuenta. El cliente está " + cliente.getEstado()));
                    }
                    Cuenta nuevaCuenta = Cuenta.builder()
                            .numeroCuenta("CTA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                            .clienteId(cliente.getId())
                            .tipoCuenta(
                                    TipoCuenta.valueOf(
                                            cuentaRequestDTO.getTipoCuenta().toUpperCase()))
                            .saldo(cuentaRequestDTO.getSaldoInicial() != null
                                    ? cuentaRequestDTO.getSaldoInicial()
                                    : BigDecimal.ZERO)
                            .estado(EstadoCuenta.valueOf(cuentaRequestDTO.getEstado().toUpperCase()))
                            .fechaCreacion(LocalDateTime.now())
                            .fechaActualizacion(LocalDateTime.now())
                            .build();

                    log.info("Guardando cuenta {} para cliente {}",
                            nuevaCuenta.getNumeroCuenta(), cliente.getNombre());
                    return cuentaRespository.save(nuevaCuenta)
                            .doOnSuccess(cuentaGuardada -> log.info("Cuenta creada exitosamente: {}",
                                    cuentaGuardada.getNumeroCuenta()));
                });
    }
}
