package com.example.clientes.service;

import org.springframework.stereotype.Service;

import com.example.clientes.entities.Cliente;
import com.example.clientes.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service("clienteService")
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private Mono<Void> validarCampo(String valor, String mensaje) {
        return (valor == null || valor.isBlank())
                ? Mono.error(new RuntimeException(mensaje))
                : Mono.empty();
    }

    // GET
    public Flux<Cliente> listarClientes() {
        log.debug("Listando todos los clientes");
        return clienteRepository.findAll()
                .switchIfEmpty(Flux.error(new RuntimeException("Lista de clientes vacía")))
                .doOnNext(c -> log.debug("Cliente listado: {}", c.getId()))
                .doOnComplete(() -> log.info("Listado de clientes completado"))
                .doOnError(e -> log.error("Error al listar clientes: {}", e.getMessage()));
    }

    public Mono<Cliente> buscarPorId(String id) {
        log.debug("Buscando por id: {}", id);
        return Mono.justOrEmpty(id)
                .filter(i -> !i.isBlank())
                .switchIfEmpty(Mono.error(new RuntimeException("ID inválido")))
                .flatMap(cliente -> {
                    return clienteRepository.findById(id);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Cliente no encontrado con id: " + id)))
                .doOnSuccess(c -> log.info("Cliente encontrado: {}", c.getNombre()))
                .doOnError(e -> log.error("Error buscando por id: {}", e.getMessage()));
    }

    public Mono<Cliente> buscarPorNombre(String nombre) {
        log.debug("Buscando por nombre: {}", nombre);
        return Mono.justOrEmpty(nombre)
                .filter(i -> !i.isBlank())
                .switchIfEmpty(Mono.error(new RuntimeException("Nombre invalido")))
                .flatMap(cliente -> {
                    return clienteRepository.findByNombre(nombre);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Cliente no encontrado: " + nombre)))
                .doOnSuccess(c -> log.info("Cliente encontrado: {}", c.getNombre()))
                .doOnError(e -> log.error("Error buscando por nombre: {}", e.getMessage()));
    }

    // POST
    public Mono<Cliente> crearCliente(Cliente cliente) {
        log.debug("Intentando crear cliente con email: {}", cliente.getEmail());

        return Mono.when(
                validarCampo(cliente.getNombre(), "Nombre inválido"),
                validarCampo(cliente.getApellido(), "Apellido inválido"),
                validarCampo(cliente.getTelefono(), "Teléfono inválido"),
                validarCampo(cliente.getEmail(), "Email inválido"),
                validarCampo(cliente.getDni(), "DNI inválido")).then(
                        Mono.zip(
                                clienteRepository.existsByEmail(cliente.getEmail()),
                                clienteRepository.existsByDni(cliente.getDni())).flatMap(result -> {
                                    if (result.getT1())
                                        return Mono.error(new RuntimeException("Email ya registrado"));
                                    if (result.getT2())
                                        return Mono.error(new RuntimeException("DNI ya registrado"));
                                    return clienteRepository.save(cliente);
                                }))
                .doOnSuccess(c -> log.info("Cliente creado con id: {}", c.getId()))
                .doOnError(e -> log.error("Error creando cliente: {}", e.getMessage()));
    }
    // PUT

    // PATCH
    public Mono<Cliente> cambiarTelefono(String id, String telefono) {
        return validarCampo(telefono, "Telefono inválido")
                .then(clienteRepository.findById(id))
                .switchIfEmpty(Mono.error(new RuntimeException("Cliente no encontrado con el id: " + id)))
                .filter(c -> !telefono.equalsIgnoreCase(c.getTelefono()))
                .switchIfEmpty(Mono.error(new RuntimeException("El telefono debe ser diferente al actual")))
                .flatMap(cliente -> {
                    cliente.setTelefono(telefono);
                    return clienteRepository.save(cliente);
                })
                .doOnSuccess(c -> log.info("Teléfono actualizado del cliente: {}", id))
                .doOnError(e -> log.error("Error al cambiar teléfono: {}", e.getMessage()));
    }

    public Mono<Cliente> cambiarEmail(String id, String email) {
        return validarCampo(email, "Email invalido")
                .then(clienteRepository.findByEmail(email))
                .switchIfEmpty(Mono.error(new RuntimeException("Cliente no encontrado con el email: " + email)))
                .filter(c -> !email.equalsIgnoreCase(c.getEmail()))
                .switchIfEmpty(Mono.error(new RuntimeException("El email debe ser diferente al actual")))
                .flatMap(cliente -> clienteRepository.existsByEmail(email)
                        .flatMap(exist -> {
                            if (exist)
                                return Mono.error(new RuntimeException("Email ya esta registrado en otro cliente"));
                            cliente.setEmail(email);
                            return clienteRepository.save(cliente);

                        }))
                .doOnSuccess(c -> log.info("Email actualizado del cliente: {}", id))
                .doOnError(e -> log.error("Error al cambiar email: {}", e.getMessage()));

    }
    // DELETE

    // ACTION

    public Mono<Cliente> desactivarCliente(String id) {
        return clienteRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException(
                "Cliente no encontrado con el id" + id)))
                .filter(c -> c.getEstado() == Cliente.EstadoCliente.INACTIVO)
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "El cliente ya esta inactivo")))
                .flatMap(cliente -> {
                    cliente.setEstado(Cliente.EstadoCliente.INACTIVO);
                    return clienteRepository.save(cliente);
                }).doOnNext(c -> log.info("Cliente desactivado {}", id))
                .doOnError(e -> log.error("Error al alterar el cliente {}", e.getMessage()));
    }

}
