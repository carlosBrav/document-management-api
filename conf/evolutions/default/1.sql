# --- !Ups
CREATE TABLE IF NOT EXISTS ACTIONS (
  `id` varchar(40) NOT NULL,
  `name` varchar(225) NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS ROL (
  `id` varchar(45) NOT NULL,
  `nombre` varchar(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS USUARIO (
  `id` varchar(45) NOT NULL,
  `usuario` varchar(45) NOT NULL,
  `password` varchar(1300) NOT NULL,
  `estado` TINYINT NOT NULL,
  `Rol_id` varchar(45) NOT NULL,
  `nombre` varchar(45) NOT NULL,
  `apellido` varchar(45) NOT NULL,
  `telefono` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS DEPENDENCIAS (
  `id` varchar(45) NOT NULL,
  `nombre` varchar(250) NOT NULL,
  `estado` varchar(45) NOT NULL,
  `siglas` varchar(45) NULL,
  `codigo` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `codigo` (`codigo` ASC))
ENGINE = InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS MOVIMIENTOS (
  `id` varchar(45) NOT NULL,
  `fecha_envio` DATETIME NOT NULL,
  `fecha_ingreso` DATETIME NOT NULL,
  `fecha_derivacion` DATETIME NOT NULL,
  `observacion` varchar(750) NULL,
  `estado_documento` varchar(45) NOT NULL,
  `estado_confirmacion` varchar(45) NOT NULL,
  `estado` varchar(45) NOT NULL,
  `Documentos_id` varchar(45) NOT NULL,
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
  `fecha_creacion` DATE NULL,
  `estado` varchar(45) NULL,
  `Tipo_docu_id` varchar(45) NOT NULL,
  `Movimientos_id` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Documentos_internos_TIPO_DOCU1_idx` (`Tipo_docu_id` ASC),
  INDEX `fk_Documentos_internos_Movimientos1_idx` (`Movimientos_id` ASC)
  )
ENGINE = InnoDB DEFAULT CHARSET=utf8;
