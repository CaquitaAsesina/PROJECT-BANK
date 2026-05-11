package com.example.cuentas.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.example.cuentas.entities.Cuenta;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CuentaRespository extends ReactiveMongoRepository<Cuenta, String> {
    Mono<Cuenta> findByNumeroCuenta(String numeroCuenta);

    Flux<Cuenta> findByClienteId(String clienteId);

    Mono<Boolean> existsByNumeroCuenta(String numeroCuenta);

    Flux<Cuenta> findByClienteIdAndEstado(String clienteId, Cuenta.EstadoCuenta estado);
}
