# --- !Ups

ALTER TABLE DOCUMENTOS_INTERNOS
  ADD user_id varchar(45) NOT NULL,
  ADD assign_to varchar(45) NOT NULL