# --- !Ups

ALTER TABLE USUARIO
  ADD isSubOfficeBoss tinyint(4) NOT NULL DEFAULT '0',
  ADD isOfficeBoss tinyint(4) NOT NULL DEFAULT '0'