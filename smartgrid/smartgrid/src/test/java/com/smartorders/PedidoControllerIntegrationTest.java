package com.smartorders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartorders.application.dto.PedidoRequestDTO;
import com.smartorders.application.dto.PedidoRequestDTO.ItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Integración - Capa Web REST Controllers")
class PedidoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/pedidos: Debe crear un pedido para cliente VIP y aplicar 15% de descuento")
    void testCrearPedidoVip() throws Exception {
        PedidoRequestDTO request = new PedidoRequestDTO(
                "CLI-VIP",
                "Entregar en oficina principal",
                List.of(
                        new ItemDTO("P-1", "Laptop HP Enterprise", 1, 2000000.0),
                        new ItemDTO("P-2", "Docking Station USB-C", 1, 500000.0)
                )
        );

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId", is("CLI-VIP")))
                .andExpect(jsonPath("$.estado", is("APROBADO")))
                .andExpect(jsonPath("$.montoBruto", is(2500000.0)))
                .andExpect(jsonPath("$.porcentajeDescuento", is(0.15)))
                .andExpect(jsonPath("$.montoDescuento", is(375000.0)))
                .andExpect(jsonPath("$.montoTotal", is(2125000.0)));
    }

    @Test
    @DisplayName("GET /api/sistema/estado: Debe retornar el estado singleton del sistema")
    void testConsultarEstadoSistema() throws Exception {
        mockMvc.perform(get("/api/sistema/estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo", is(true)))
                .andExpect(jsonPath("$.tasaIva", is(0.19)));
    }

    @Test
    @DisplayName("POST /api/pedidos: Debe rechazar crédito para cliente moroso")
    void testCrearPedidoMorosoRechazado() throws Exception {
        PedidoRequestDTO request = new PedidoRequestDTO(
                "CLI-MOROSO",
                "Pedido a crédito",
                List.of(new ItemDTO("P-3", "Televisor 55", 1, 1800000.0))
        );

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado", is("RECHAZADO")))
                .andExpect(jsonPath("$.motivoRechazo", notNullValue()));
    }
}
