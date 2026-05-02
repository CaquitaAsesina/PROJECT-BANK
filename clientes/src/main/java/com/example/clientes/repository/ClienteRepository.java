package com.example.clientes.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.example.clientes.entities.Cliente;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ClienteRepository extends ReactiveMongoRepository<Cliente, String> {
    Mono<Cliente> findByNombre(String nombre);

    Mono<Cliente> findByEmail(String email);

    Mono<Cliente> findByDni(String dni);

    Flux<Cliente> findByEstado(Cliente.EstadoCliente estado);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByDni(String dni);
}
