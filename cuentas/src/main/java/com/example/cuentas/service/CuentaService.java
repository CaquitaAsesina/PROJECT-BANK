package com.example.cuentas.service;

import org.springframework.stereotype.Service;

import com.example.cuentas.entities.Cuenta;
import com.example.cuentas.repository.CuentaRespository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRespository cuentaRespository;

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
}
