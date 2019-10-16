# --- !Ups

ALTER TABLE MOVIMIENTOS
  ADD docu_nombre VARCHAR(40),
  ADD docu_num VARCHAR(40),
  ADD docu_siglas VARCHAR(40),
  ADD docu_anio VARCHAR(40);