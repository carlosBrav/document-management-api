# --- !Ups
CREATE TABLE IF NOT EXISTS ROL (
  `id` varchar(45) NOT NULL,
  `nombre` varchar(45) NOT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS USUARIO (
  `id` varchar(45) NOT NULL,
  `usuario` varchar(45) NOT NULL,
  `password` varchar(1300) NOT NULL,
  `estado` tinyint(4) NOT NULL DEFAULT '1',
  `Rol_id` varchar(45) NOT NULL,
  `nombre` varchar(45) NOT NULL,
  `apellido` varchar(45) NOT NULL,
  `telefono` varchar(45) NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Dependencia_id` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS DEPENDENCIAS (
  `id` varchar(45) NOT NULL,
  `nombre` varchar(250) NOT NULL,
  `estado` tinyint(4) NOT NULL DEFAULT '1',
  `siglas` varchar(45) NULL,
  `codigo` varchar(45) NOT NULL,
  `tipo` varchar(10) NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `codigo` (`codigo` ASC))
ENGINE = InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS MOVIMIENTOS (
  `id` varchar(45) NOT NULL,
  `fecha_envio` timestamp NULL,
  `fecha_ingreso` timestamp NULL,
  `fecha_derivacion` timestamp NULL,
  `observacion` varchar(750) NULL,
  `estado_documento` varchar(45) NOT NULL,
  `estado_confirmacion` tinyint(4) NOT NULL DEFAULT '0',
  `Documentos_internos_id` varchar(45) NULL,
  `Dependencias_id` varchar(45) NOT NULL,
  `Dependencias_id1` varchar(45) NOT NULL,
  `asignado_a` varchar(45) NULL,
  `Usuario_id` varchar(45) NOT NULL,
  `num_tram` varchar(45) NULL,
  `indi_nombre` varchar(45) NULL,
  `indi_cod` VARCHAR(12) NULL,
  `movimiento` INT NULL DEFAULT 0,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_Movimientos_Dependencias1_idx` (`Dependencias_id` ASC),
  INDEX `fk_Movimientos_Dependencias2_idx` (`Dependencias_id1` ASC),
  INDEX `fk_Movimientos_Usuario1_idx` (`Usuario_id` ASC),
  UNIQUE INDEX `num_tram_movimiento` (`num_tram` ASC, `movimiento` ASC)
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS TIPO_DOCUMENTO (
  `id` varchar(45) NOT NULL,
  `nombre_tipo` varchar(45) NULL,
  `flag1` varchar(45) NULL,
  `flag2` varchar(45) NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS DOCUMENTOS_INTERNOS (
  `id` varchar(45) NOT NULL,
  `estado` varchar(45) NULL,
  `Tipo_docu_id` varchar(45) NOT NULL,
  `num_documento` varchar(45) NOT NULL,
  `siglas` VARCHAR(12) NOT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_Documentos_internos_TIPO_DOCU1_idx` (`Tipo_docu_id` ASC)
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE vista2 (
  `TRAM_NUM` varchar(25) NOT NULL,
  `MOVI_NUM` varchar(45) DEFAULT NULL,
  `MOVI_ORIGEN` varchar(350) DEFAULT NULL,
  `DEPE_COD` varchar(45) DEFAULT NULL,
  `MOVI_DESTINO` varchar(350) DEFAULT NULL,
  `DEST_COD` varchar(45) DEFAULT NULL,
  `MOVI_FEC_ENV` timestamp NULL,
  `MOVI_FEC_ING` timestamp NULL,
  `INDI_NOMBRE` varchar(45) DEFAULT NULL,
  `INDI_COD` varchar(45) DEFAULT NULL,
  `MOVI_OBS` varchar(450) DEFAULT NULL,
  `ESTA_NOMBRE` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE vista1 (
  `TRAM_NUM` varchar(20) DEFAULT NULL,
  `TRAM_FECHA` timestamp NULL,
  `DEPE_ORIGEN` varchar(250) DEFAULT NULL,
  `DEPE_COD` varchar(45) DEFAULT NULL,
  `TRAM_OBS` varchar(250) DEFAULT NULL,
  `ESTA_DESCRIP` varchar(45) DEFAULT NULL,
  `USU` varchar(45) DEFAULT NULL,
  `USU_NOM` varchar(45) DEFAULT NULL,
  `DOCU_PRIC` varchar(45) DEFAULT NULL,
  `DOCU_NOMBRE` varchar(45) DEFAULT NULL,
  `DOCU_NUM` varchar(45) DEFAULT NULL,
  `DOCU_SIGLAS` varchar(45) DEFAULT NULL,
  `DOCU_ANIO` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;