package com.example.clientes.service;

import org.springframework.stereotype.Service;

import com.example.clientes.entities.Cliente;
import com.example.clientes.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Mono<Cliente> crearCliente(Cliente cliente) {
        log.debug("Intentando crear cliente con email: {}", cliente.getEmail());
        return clienteRepository.existsByEmail(cliente.getEmail()).flatMap(existeEmail -> {
            if (existeEmail) {
                return Mono.error(new RuntimeException(
                        "Ya existe un cliente con el email: " + cliente.getEmail()));
            }
            return clienteRepository.existsByDni(cliente.getDni());
        }).flatMap(existeDni -> {
            if (existeDni) {
                return Mono.error(new RuntimeException(
                        "Ya existe un cliente con el DNI: " + cliente.getDni()));
            }
            return clienteRepository.save(cliente);
        }).doOnSuccess(c -> log.info("Cliente creado exitosamente con id: {}", c.getId()))
                .doOnError(e -> log.error("Error al crear cliente: {}", e.getMessage()));
    }
}
