# --- !Ups

ALTER TABLE MOVIMIENTOS ADD UNIQUE `unique_index`(`num_tram`, `movimiento`, `fecha_ingreso`);
ALTER TABLE MOVIMIENTOS ADD CONSTRAINT `fk_Movimientos_Dependencias1`
    FOREIGN KEY (`Dependencias_id`)
    REFERENCES `Dependencias` (`id`);

ALTER TABLE MOVIMIENTOS ADD CONSTRAINT `fk_Movimientos_Dependencias2`
    FOREIGN KEY (`Dependencias_id1`)
    REFERENCES `Dependencias` (`id`);

ALTER TABLE MOVIMIENTOS ADD CONSTRAINT `fk_Movimientos_Usuario1`
    FOREIGN KEY (`Usuario_id`)
    REFERENCES `Usuario` (`id`);

ALTER TABLE MOVIMIENTOS ADD CONSTRAINT `fk_Movimientos_Documentos_internos1`
    FOREIGN KEY (`Documentos_internos_id`)
    REFERENCES `Documentos_internos` (`id`);