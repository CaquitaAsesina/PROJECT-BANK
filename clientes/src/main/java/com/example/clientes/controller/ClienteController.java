package com.example.clientes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clientes.service.ClienteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor

public class ClienteController {
    @Qualifier("clienteService")
    @Autowired
    private ClienteService clienteService;

}
