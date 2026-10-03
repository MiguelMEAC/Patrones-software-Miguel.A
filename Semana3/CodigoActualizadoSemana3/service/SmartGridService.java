package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.config.SmartGridConfig;
import org.springframework.stereotype.Service;

@Service
public class SmartGridService {

    private final SmartGridConfig config;

    public SmartGridService(SmartGridConfig config) {
        this.config = config;
    }

    public String obtenerEstado() {

        if (config.isSistemaActivo()) {
            return "SmartGrid está activo";
        }

        return "SmartGrid está inactivo";
    }

    public String comprobarSingleton() {

        SmartGridConfig config1 =
                SmartGridConfig.getInstancia();

        SmartGridConfig config2 =
                SmartGridConfig.getInstancia();

        return "¿Es la misma instancia? "
                + (config1 == config2);
    }
}