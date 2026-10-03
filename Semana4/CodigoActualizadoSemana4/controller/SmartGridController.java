package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.service.SmartGridService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmartGridController {

    private final SmartGridService service;

    public SmartGridController(SmartGridService service) {
        this.service = service;
    }

    @GetMapping("/smartgrid/estado")
    public String obtenerEstado() {
        return service.obtenerEstado();
    }

    @GetMapping("/smartgrid/singleton")
    public String comprobarSingleton() {
        return service.comprobarSingleton();
    }
}