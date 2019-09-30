# --- !Ups

ALTER TABLE USUARIO ADD CONSTRAINT `fk_Usuario_Rol`
    FOREIGN KEY (`Rol_id`) REFERENCES `Rol` (`id`);

ALTER TABLE USUARIO ADD CONSTRAINT `usuario_unique` UNIQUE (`usuario`);

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

ALTER TABLE USUARIO ADD CONSTRAINT `fk_usuario_ofi`
    FOREIGN KEY (`Dependencia_id`) REFERENCES `DEPENDENCIAS` (`id`);
