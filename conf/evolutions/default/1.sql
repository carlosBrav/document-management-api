
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Table 'Persona'
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS 'Persona' (
  'idPersona' INT NOT NULL,
  'Nombre' VARCHAR(45) NOT NULL,
  'Apellido' VARCHAR(45) NOT NULL,
  'Sexo' VARCHAR(45) NULL,
  PRIMARY KEY ('idPersona'))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table 'Rol'
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS 'Rol' (
  'idRol' INT NOT NULL,
  'nombreRol' VARCHAR(45) NOT NULL,
  PRIMARY KEY ('idRol'))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table 'Usuario'
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS 'Usuario' (
  'idUsuario' INT NOT NULL,
  'nombre' VARCHAR(45) NOT NULL,
  'password' VARCHAR(1300) NOT NULL,
  'estado' TINYINT NOT NULL,
  'Persona_idPersona' INT NOT NULL,
  'Rol_idRol' INT NOT NULL,
  PRIMARY KEY ('idUsuario'),
  INDEX 'fk_Usuario_Persona_idx' ('Persona_idPersona' ASC),
  INDEX 'fk_Usuario_Rol1_idx' ('Rol_idRol' ASC),
  CONSTRAINT 'fk_Usuario_Persona'
  FOREIGN KEY ('Persona_idPersona')
  REFERENCES 'Persona' ('idPersona')
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT 'fk_Usuario_Rol1'
  FOREIGN KEY ('Rol_idRol')
  REFERENCES 'Rol' ('idRol')
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table 'Dependencias'
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS 'Dependencias' (
  'idDependencia' INT NOT NULL,
  'nombreDependencia' VARCHAR(250) NOT NULL,
  'estado' TINYINT NOT NULL,
  'siglas' VARCHAR(45) NULL,
  'codDependencia' VARCHAR(45) NOT NULL,
  PRIMARY KEY ('idDependencia'))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table 'Movimientos'
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS 'Movimientos' (
  'idMovimientos' INT NOT NULL,
  'Fecha_envio' DATETIME NOT NULL,
  'Fecha_ingreso' DATETIME NOT NULL,
  'Fecha_derivacion' DATETIME NOT NULL,
  'Observacion' VARCHAR(750) NULL,
  'Estado_documento' VARCHAR(45) NOT NULL,
  'Estado_confirmacion' VARCHAR(45) NOT NULL,
  'Estado' INT NOT NULL,
  'Documentos_idDocumentos' INT NOT NULL,
  'Dependencias_idDependencia' INT NOT NULL,
  'Dependencias_idDependencia1' INT NOT NULL,
  'asignado_a' VARCHAR(45) NULL,
  'Usuario_idUsuario' INT NOT NULL,
  PRIMARY KEY ('idMovimientos'),
  INDEX 'fk_Movimientos_Dependencias1_idx' ('Dependencias_idDependencia' ASC),
  INDEX 'fk_Movimientos_Dependencias2_idx' ('Dependencias_idDependencia1' ASC),
  INDEX 'fk_Movimientos_Usuario1_idx' ('Usuario_idUsuario' ASC),
  CONSTRAINT 'fk_Movimientos_Dependencias1'
  FOREIGN KEY ('Dependencias_idDependencia')
  REFERENCES 'Dependencias' ('idDependencia')
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT 'fk_Movimientos_Dependencias2'
  FOREIGN KEY ('Dependencias_idDependencia1')
  REFERENCES 'Dependencias' ('idDependencia')
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT 'fk_Movimientos_Usuario1'
  FOREIGN KEY ('Usuario_idUsuario')
  REFERENCES 'Usuario' ('idUsuario')
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table 'TIPO_DOCU'
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS 'TIPO_DOCU' (
  'idTIPO_DOCU' INT NOT NULL,
  'nombre_tipo' VARCHAR(45) NULL,
  'flag1' VARCHAR(45) NULL,
  'flag2' VARCHAR(45) NULL,
  PRIMARY KEY ('idTIPO_DOCU'))
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table 'Documentos_internos'
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS 'Documentos_internos' (
  'idDocumentos_internos' INT NOT NULL,
  'fechaCreacion' DATE NULL,
  'estado' VARCHAR(45) NULL,
  'TIPO_DOCU_idTIPO_DOCU' INT NOT NULL,
  'Movimientos_idMovimientos' INT NOT NULL,
  PRIMARY KEY ('idDocumentos_internos'),
  INDEX 'fk_Documentos_internos_TIPO_DOCU1_idx' ('TIPO_DOCU_idTIPO_DOCU' ASC),
  INDEX 'fk_Documentos_internos_Movimientos1_idx' ('Movimientos_idMovimientos' ASC),
  CONSTRAINT 'fk_Documentos_internos_TIPO_DOCU1'
  FOREIGN KEY ('TIPO_DOCU_idTIPO_DOCU')
  REFERENCES 'TIPO_DOCU' ('idTIPO_DOCU')
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT 'fk_Documentos_internos_Movimientos1'
  FOREIGN KEY ('Movimientos_idMovimientos')
  REFERENCES 'Movimientos' ('idMovimientos')
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
