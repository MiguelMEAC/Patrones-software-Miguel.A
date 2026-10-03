-- =========================================================================
-- SISTEMA DE GESTION INTELIGENTE DE PEDIDOS, CREDITO Y FACTURACION (SmartOrders)
-- Base de Datos Relacional: PostgreSQL 16+
-- Autor: Equipo de Ingenieria UTS - Patrones de Software
-- =========================================================================

-- Limpieza preventiva
DROP TABLE IF EXISTS pedido_items CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;

-- 1. Tabla de Clientes
CREATE TABLE clientes (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    limite_credito NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    edad INTEGER NOT NULL DEFAULT 18,
    ocupacion VARCHAR(100),
    telefono VARCHAR(50),
    estrato INTEGER NOT NULL DEFAULT 3,
    nit_o_rut VARCHAR(50) NOT NULL,
    regimen_fiscal VARCHAR(50) NOT NULL DEFAULT 'Comun',
    compras_historicas_count INTEGER NOT NULL DEFAULT 0,
    pagos_al_dia BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. Tabla de Pedidos (Agregado Raiz)
CREATE TABLE pedidos (
    id VARCHAR(50) PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    cliente_id VARCHAR(50) NOT NULL REFERENCES clientes(id) ON DELETE RESTRICT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT,
    monto_bruto NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    porcentaje_descuento NUMERIC(5, 4) NOT NULL DEFAULT 0.0000,
    monto_descuento NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    monto_total NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    motivo_rechazo TEXT
);

-- 3. Tabla de Items de Pedido (Detalle)
CREATE TABLE pedido_items (
    id BIGSERIAL PRIMARY KEY,
    pedido_id VARCHAR(50) NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    producto_id VARCHAR(50) NOT NULL,
    nombre_producto VARCHAR(150) NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(15, 2) NOT NULL CHECK (precio_unitario >= 0),
    subtotal NUMERIC(15, 2) NOT NULL
);

-- Indices para alto desempeno
CREATE INDEX idx_pedidos_cliente ON pedidos(cliente_id);
CREATE INDEX idx_pedidos_estado ON pedidos(estado);
CREATE INDEX idx_items_pedido ON pedido_items(pedido_id);

-- =========================================================================
-- DATOS SEMILLA (Seed Data) PARA PRUEBAS Y SUSTENTACION
-- =========================================================================

-- Cliente VIP (Descuento VIP 15%)
INSERT INTO clientes (id, nombre, email, limite_credito, edad, ocupacion, telefono, estrato, nit_o_rut, regimen_fiscal, compras_historicas_count, pagos_al_dia)
VALUES ('CLI-VIP', 'Carlos Mendoza (VIP)', 'carlos.mendoza@email.com', 15000000.00, 45, 'Empresario', '+57 311 000 1111', 4, '900.123.456-1', 'Comun', 12, TRUE);

-- Cliente Subsidio (Descuento Subsidio 20% por estrato < 2)
INSERT INTO clientes (id, nombre, email, limite_credito, edad, ocupacion, telefono, estrato, nit_o_rut, regimen_fiscal, compras_historicas_count, pagos_al_dia)
VALUES ('CLI-SUBSIDIO', 'Ana Gomez (Subsidio)', 'ana.gomez@email.com', 3000000.00, 28, 'Estudiante', '+57 312 222 3333', 1, '1.098.765.432', 'Simplificado', 2, TRUE);

-- Cliente Frecuente (Descuento Frecuente 10% por >5 compras recientes)
INSERT INTO clientes (id, nombre, email, limite_credito, edad, ocupacion, telefono, estrato, nit_o_rut, regimen_fiscal, compras_historicas_count, pagos_al_dia)
VALUES ('CLI-FRECUENTE', 'Julian Rueda (Frecuente)', 'julian.rueda@email.com', 5000000.00, 35, 'Ingeniero', '+57 315 444 5555', 3, '88.777.666', 'Comun', 6, TRUE);

-- Cliente Senior (Descuento Senior 5% por edad > 65)
INSERT INTO clientes (id, nombre, email, limite_credito, edad, ocupacion, telefono, estrato, nit_o_rut, regimen_fiscal, compras_historicas_count, pagos_al_dia)
VALUES ('CLI-SENIOR', 'Martha Lucia Ortiz (Senior)', 'martha.ortiz@email.com', 4000000.00, 72, 'Pensionada', '+57 318 777 8888', 3, '28.333.444', 'Simplificado', 3, TRUE);

-- Cliente Regular (Descuento 0%)
INSERT INTO clientes (id, nombre, email, limite_credito, edad, ocupacion, telefono, estrato, nit_o_rut, regimen_fiscal, compras_historicas_count, pagos_al_dia)
VALUES ('CLI-REGULAR', 'David Torres (Regular)', 'david.torres@email.com', 2000000.00, 29, 'Disenador', '+57 320 999 0000', 3, '1.095.123.789', 'Comun', 1, TRUE);

-- Cliente Moroso (Rechazo automatico por mora crediticia)
INSERT INTO clientes (id, nombre, email, limite_credito, edad, ocupacion, telefono, estrato, nit_o_rut, regimen_fiscal, compras_historicas_count, pagos_al_dia)
VALUES ('CLI-MOROSO', 'Hector Salamanca (En Mora)', 'hector.salamanca@email.com', 500000.00, 55, 'Comerciante', '+57 300 666 9999', 2, '13.888.999', 'Comun', 1, FALSE);
