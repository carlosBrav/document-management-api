package models

import java.sql.Timestamp

import scala.reflect.ClassTag
import slick.jdbc.MySQLProfile.api._

abstract class BaseEntityTable[E: ClassTag](tag: Tag, tableName: String) extends Table[E](tag, tableName) {
  val id: Rep[String] = column[String]("ID", O.PrimaryKey)
  val fechaCreacion: Rep[Timestamp] = column[Timestamp]("FECHA_CREACION")
  val fechaModificacion: Rep[Timestamp] = column[Timestamp]("FECHA_MODIFICACION")
}

class RolTable(tag: Tag) extends BaseEntityTable[Rol](tag,"ROL") {
  def nombre = column[String]("NOMBRE")

  def * =
    (id.?, nombre, fechaCreacion.?, fechaModificacion.?) <>
      (Rol.tupled, Rol.unapply)
}

class UsuarioTable(tag: Tag) extends BaseEntityTable[Usuario](tag,"USUARIO") {
  def usuario = column[String]("USUARIO")
  def password = column[String]("PASSWORD")
  def estado = column[Boolean]("ESTADO")
  def rolId = column[String]("ROL_ID")
  def nombre = column[String]("NOMBRE")
  def apellido = column[String]("APELLIDO")
  def telefono = column[String]("TELEFONO")

  def * =
    (id.?, usuario,password,estado,rolId,nombre,apellido,telefono.?,
    fechaCreacion.?,fechaModificacion.?) <>
      (Usuario.tupled, Usuario.unapply)
}

class MovimientoTable(tag: Tag) extends BaseEntityTable[Movimientos](tag,"MOVIMIENTOS") {
  def movimiento= column[Int]("MOVIMIENTO")
  def numTram= column[String]("NUM_TRAM")
  def estadoDocumento= column[String]("ESTADO_DOCUMENTO")
  def estadoConfirmacion= column[Boolean]("ESTADO_CONFIRMACION")
  def documentosInternosId= column[String]("DOCUMENTOS_INTERNOS_ID")
  def dependenciasId= column[String]("DEPENDENCIAS_ID")
  def dependenciasId1= column[String]("DEPENDENCIAS_ID1")
  def asignadoA= column[String]("ASIGNADO_A")
  def usuarioId= column[String]("USUARIO_ID")
  def fechaIngreso= column[Timestamp]("FECHA_INGRESO")
  def fechaDerivacion= column[Timestamp]("FECHA_DERIVACION")
  def fechaEnvio= column[Timestamp]("FECHA_ENVIO")
  def observacion= column[String]("OBSERVACION")

  def * =
    (id.?,movimiento,numTram,estadoDocumento,estadoConfirmacion,documentosInternosId.?,dependenciasId,dependenciasId1,
      asignadoA.?,usuarioId,fechaIngreso.?,fechaDerivacion.?,fechaEnvio.?,observacion.?,
      fechaCreacion.?, fechaModificacion.?) <>
      (Movimientos.tupled, Movimientos.unapply)
}

class DependenciaTable(tag: Tag) extends BaseEntityTable[Dependencias](tag,"DEPENDENCIAS") {
  def nombre= column[String]("NOMBRE")
  def estado= column[Boolean]("ESTADO")
  def siglas= column[String]("SIGLAS")
  def codigo= column[String]("CODIGO")

  def * = (id.?, nombre, estado, siglas.?, codigo, fechaCreacion.?, fechaModificacion.?) <>
    (Dependencias.tupled, Dependencias.unapply)
}

class TipoDocumentoTable(tag: Tag) extends BaseEntityTable[TipoDocumento](tag,"TIPO_DOCUMENTO") {
  def nombreTipo= column[String]("NOMBRE_TIPO")
  def flag1= column[String]("FLAG1")
  def flag2= column[String]("FLAG2")

  def * = (id.?, nombreTipo, flag1.?, flag2.?, fechaCreacion.?, fechaModificacion.?) <>
    (TipoDocumento.tupled, TipoDocumento.unapply)
}

class DocumentoInternoTable(tag: Tag) extends BaseEntityTable[DocumentosInternos](tag,"DOCUMENTOS_INTERNOS") {
  def estado =column[Boolean]("ESTADO")
  def tipoDocuId = column[String]("TIPO_DOCU_ID")

  def * = (id.?, fechaCreacion.?, fechaModificacion.?, estado, tipoDocuId) <>
    (DocumentosInternos.tupled, DocumentosInternos.unapply)
}