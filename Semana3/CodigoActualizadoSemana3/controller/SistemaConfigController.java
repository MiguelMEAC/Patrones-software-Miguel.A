package com.smartorders.infrastructure.adapters.in.web;

import com.smartorders.application.dto.EstadoSistemaDTO;
import com.smartorders.infrastructure.config.SistemaConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sistema")
@CrossOrigin(origins = "*")
public class SistemaConfigController {

    @GetMapping("/estado")
    public ResponseEntity<EstadoSistemaDTO> obtenerEstado() {
        SistemaConfig config = SistemaConfig.getInstancia();
        return ResponseEntity.ok(new EstadoSistemaDTO(
                config.getNombreSistema(),
                config.isSistemaActivo(),
                config.getTasaIva(),
                config.getLimiteMaximoCreditoPorDefecto(),
                config.isPermitirVentaSinStock()
        ));
    }

    @PostMapping("/estado")
    public ResponseEntity<EstadoSistemaDTO> cambiarEstado(
            @RequestParam boolean activo,
            @RequestParam(required = false) String nombre) {
        SistemaConfig config = SistemaConfig.getInstancia();
        config.setSistemaActivo(activo);
        if (nombre != null && !nombre.isBlank()) {
            config.setNombreSistema(nombre);
        }
        return obtenerEstado();
    }
}
