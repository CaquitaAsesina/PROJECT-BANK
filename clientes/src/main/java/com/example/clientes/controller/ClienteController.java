package com.example.clientes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.clientes.entities.Cliente;
import com.example.clientes.service.ClienteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor

public class ClienteController {
    @Qualifier("clienteService")
    @Autowired
    private ClienteService clienteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Cliente> crearCliente(@RequestBody Cliente cliente) {
        return clienteService.crearCliente(cliente);
    }

    @GetMapping
    public Flux<Cliente>listar(){
        return clienteService.listarClientes();
    }
    @GetMapping("/{id}")
    public Mono<Cliente> buscarPorId(@PathVariable String id) {
        return clienteService.buscarPorId(id);
    }

    @GetMapping("/buscar")
    public Mono<Cliente> buscarPorNombre(@PathVariable String nombre) {
        return clienteService.buscarPorNombre(nombre);
    }

    @PatchMapping("/{id}/telefono")
    public Mono<Cliente> cambiarTelefono(@PathVariable String id, @RequestBody Cliente cliente) {
        return clienteService.cambiarTelefono(id, cliente.getTelefono());
    }

    @PatchMapping("/{id}/email")
    public Mono<Cliente> cambiarEmail(@PathVariable String id, @RequestBody Cliente cliente) {
        return clienteService.cambiarEmail(id, cliente.getEmail());
    }

}
