# --- !Ups

INSERT INTO `ROL`(`id`, `nombre`, `fecha_creacion`, `fecha_modificacion`)
VALUES ('12345', 'ADMINISTRADOR', '2019-09-14 20:05:45','2019-09-14 20:05:45');


INSERT INTO `USUARIO` (`id`, `usuario`, `password`, `estado`, `Rol_id`, `nombre`, `apellido`, `telefono`, `fecha_creacion`, `fecha_modificacion`)
VALUES ('12345','cbravo', '$2a$10$mB0cqt5OA5J6udJSgMNXN.cH1LllWW3eWTblJm9iY5GR.dZetEIlC', '1', '12345', 'carlos', 'bravo', '956359930', '2019-09-14 20:07:30', '2019-09-14 20:07:30');
