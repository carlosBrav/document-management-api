# --- !Ups

ALTER TABLE MOVIMIENTOS ADD UNIQUE `unique_index`(`num_tram`, `movimiento`, `fecha_ingreso`);