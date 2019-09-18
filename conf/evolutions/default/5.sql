# --- !Ups

INSERT INTO `ROL`(`id`, `nombre`, `fecha_creacion`, `fecha_modificacion`)
VALUES ('12345', 'ADMINISTRADOR', '2019-09-14 20:05:45','2019-09-14 20:05:45');


INSERT INTO `USUARIO` (`id`, `usuario`, `password`, `estado`, `Rol_id`, `nombre`, `apellido`, `telefono`, `fecha_creacion`, `fecha_modificacion`)
VALUES ('12345','cbravo', '$2a$10$mB0cqt5OA5J6udJSgMNXN.cH1LllWW3eWTblJm9iY5GR.dZetEIlC', '1', '12345', 'carlos', 'bravo', '956359930', '2019-09-14 20:07:30', '2019-09-14 20:07:30');


Insert into `VISTA1` (`TRAM_NUM`,`TRAM_FECHA`,`DEPE_ORIGEN`,`DEPE_COD`,`TRAM_OBS`,`ESTA_DESCRIP`,`USU`,`USU_NOM`,`DOCU_PRIC`,`DOCU_NOMBRE`,`DOCU_NUM`,`DOCU_SIGLAS`,`DOCU_ANIO`)
values ('00082-OGPL-2019',STR_TO_DATE('21/01/19','%d/%m/%Y'),'OFICINA GENERAL DE PLANIFICACION - OGPL','100392','REGISTRO DE INGRESO  DIA 22.01.19','En proceso','csandovalm','SANDOVAL MELGAREJO, CATHERINE MARLENE','1','OFICIO(S)','00177','OGPL','2019');
