# --- !Ups

ALTER TABLE USUARIO ADD CONSTRAINT `fk_Usuario_Rol`
    FOREIGN KEY (`Rol_id`) REFERENCES `Rol` (`id`);

ALTER TABLE USUARIO ADD CONSTRAINT `usuario_unique` UNIQUE (`usuario`)