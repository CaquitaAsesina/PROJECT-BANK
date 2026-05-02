package com.example.clientes.service;

import org.springframework.stereotype.Service;

import com.example.clientes.entities.Cliente;
import com.example.clientes.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
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

    public Mono<Cliente> buscarPorId(String id) {
        log.debug("Buscando cliente por id: {}", id);
        return clienteRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException(
                "Cliente no encontrado con id" + id)))
                .doOnSuccess(c -> log.debug("Cliente encontrado {}", c.getNombre()));
    }

    public Mono<Cliente> buscarPorNombre(String nombre) {
        log.debug("Buscando por nombre: {}", nombre);
        return clienteRepository.findByNombre(nombre).switchIfEmpty(Mono.error(new RuntimeException(
                "Cliente no encontrado con el nombre" + nombre)))
                .doOnSuccess(c -> log.debug("Cliente encontrado {}", c.getNombre()));
    }

    public Flux<Cliente> listarClientesActivos() {
        log.debug("Listando todos los clientes activos");
        return clienteRepository.findByEstado(Cliente.EstadoCliente.ACTIVO)
                .doOnNext(c -> log.debug("Cliente activo {}", c.getId()))
                .doOnComplete(() -> log.debug("Listado de clientes completados"));
    }

    public Mono<Cliente> desactivarCliente(String id) {
        return clienteRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException(
                "Cliente no encontrado con el id" + id))).flatMap(cliente -> {
                    if (cliente.getEstado() == Cliente.EstadoCliente.ACTIVO) {
                        return Mono.error(new RuntimeException(
                                "Cliente ya esta inactivo"));
                    }
                    cliente.setEstado(Cliente.EstadoCliente.INACTIVO);
                    return clienteRepository.save(cliente);
                }).doOnSuccess(c -> log.info("Cliente desactivado {}", id));
    }

    public Mono<Cliente> cambiarTelefono(String id, String telefono) {
        return clienteRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException(
                "Cliente no encontrado por el id" + id))).flatMap(cliente -> {
                    if (telefono == null || telefono.isBlank()) {
                        return Mono.error(new RuntimeException(
                                "Telefono invalido"));
                    }
                    if (cliente.getTelefono() != null && telefono.equalsIgnoreCase(cliente.getTelefono())) {
                        return Mono.error(new RuntimeException(
                                "El telefono es el mismo"));
                    }
                    cliente.setTelefono(telefono);
                    return clienteRepository.save(cliente);
                }).doOnSuccess(c -> log.info("Telefono actualizado del cliente {}", id));
    }
}
