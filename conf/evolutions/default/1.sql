# --- !Ups
CREATE TABLE IF NOT EXISTS ROL (
  `id` varchar(45) NOT NULL,
  `nombre` varchar(45) NOT NULL,
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
  PRIMARY KEY (`id`)
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS DEPENDENCIAS (
  `id` varchar(45) NOT NULL,
  `nombre` varchar(250) NOT NULL,
  `estado` tinyint(4) NOT NULL DEFAULT '1',
  `siglas` varchar(45) NULL,
  `codigo` varchar(45) NOT NULL,
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
  `num_tram` varchar(45) NOT NULL,
  `movimiento` INT NOT NULL,
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
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS DOCUMENTOS_INTERNOS (
  `id` varchar(45) NOT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` varchar(45) NULL,
  `Tipo_docu_id` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Documentos_internos_TIPO_DOCU1_idx` (`Tipo_docu_id` ASC)
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;
