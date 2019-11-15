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
  def dependenciaId = column[String]("DEPENDENCIA_ID")
  def isSubOfficeBoss = column[Boolean]("ISSUBOFFICEBOSS")
  def isOfficeBoss = column[Boolean]("ISOFFICEBOSS")
  def * =
    (id.?, usuario,password,estado,rolId,nombre,apellido,telefono.?,dependenciaId.?,isSubOfficeBoss, isOfficeBoss,
    fechaCreacion.?,fechaModificacion.?) <>
      (Usuario.tupled, Usuario.unapply)
}

class MovimientoTable(tag: Tag) extends BaseEntityTable[Movimientos](tag,"MOVIMIENTOS") {
  def movimiento= column[Int]("MOVIMIENTO")
  def numTram= column[String]("NUM_TRAM")
  def estadoDocumento= column[String]("ESTADO_DOCUMENTO")
  def documentosInternosId= column[String]("DOCUMENTOS_INTERNOS_ID")
  def dependenciasId= column[String]("DEPENDENCIAS_ID")
  def dependenciasId1= column[String]("DEPENDENCIAS_ID1")
  def asignadoA= column[String]("ASIGNADO_A")
  def usuarioId= column[String]("USUARIO_ID")
  def fechaIngreso= column[Timestamp]("FECHA_INGRESO")
  def indiNombre = column[String]("INDI_NOMBRE")
  def indiCod = column[String]("INDI_COD")
  def docuNombre = column[String]("DOCU_NOMBRE")
  def docuNum = column[String]("DOCU_NUM")
  def docuSiglas = column[String]("DOCU_SIGLAS")
  def docuAnio = column[String]("DOCU_ANIO")
  def fechaEnvio= column[Timestamp]("FECHA_ENVIO")
  def observacion= column[String]("OBSERVACION")

  def * =
    (id.?,movimiento.?,numTram.?,estadoDocumento,documentosInternosId.?,dependenciasId,dependenciasId1,
      asignadoA.?,usuarioId,fechaIngreso.?,fechaEnvio.?,observacion.?,indiNombre.?, indiCod.?, docuNombre.?,
      docuNum.?, docuSiglas.?, docuAnio.?, fechaCreacion.?, fechaModificacion.?) <>
      (Movimientos.tupled, Movimientos.unapply)
}

class DependenciaTable(tag: Tag) extends BaseEntityTable[Dependencias](tag,"DEPENDENCIAS") {
  def nombre= column[String]("NOMBRE")
  def estado= column[Boolean]("ESTADO")
  def siglas= column[String]("SIGLAS")
  def codigo= column[String]("CODIGO")
  def tipo= column[String]("TIPO")
  def * = (id.?, nombre, estado, siglas.?, codigo, tipo.?, fechaCreacion.?, fechaModificacion.?) <>
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
  def estadoDocumento =column[String]("ESTADO_DOCUMENTO")
  def tipoDocuId = column[String]("TIPO_DOCU_ID")
  def numDocumento = column[Int]("NUM_DOCUMENTO")
  def siglas = column[String]("SIGLAS")
  def anio = column[String]("ANIO")
  def asunto = column[String]("ASUNTO")
  def observacion = column[String]("OBSERVACION")
  def origenId = column[String]("ORIGEN_ID")
  def destinoId = column[String]("DESTINO_ID")
  def active = column[Boolean]("ACTIVE")
  def userId = column[String]("USER_ID")
  def firma = column[String]("FIRMA")
  def * = (id.?, estadoDocumento.?, tipoDocuId,numDocumento.?,siglas.?,anio.?,asunto.?, observacion.?,origenId, destinoId.?,active,
    userId.?, firma.?,fechaCreacion.?, fechaModificacion.?) <>
    (DocumentosInternos.tupled, DocumentosInternos.unapply)
}

class Vista1Table(tag: Tag) extends Table[Vista1](tag, "VISTA1") {
  def tramNum = column[String]("TRAM_NUM")
  def tramFecha = column[Timestamp]("TRAM_FECHA")
  def depeOrigen = column[String]("DEPE_ORIGEN")
  def depeCod = column[String]("DEPE_COD")
  def tramObs = column[String]("TRAM_OBS")
  def estaDescrip = column[String]("ESTA_DESCRIP")
  def usu  = column[String]("USU")
  def usuNom = column[String]("USU_NOM")
  def docuPric = column[String]("DOCU_PRIC")
  def docuNombre = column[String]("DOCU_NOMBRE")
  def docuNum = column[String]("DOCU_NUM")
  def docuSiglas = column[String]("DOCU_SIGLAS")
  def docuAnio = column[String]("DOCU_ANIO")

  def * = (tramNum, tramFecha.?, depeOrigen, depeCod, tramObs.?, estaDescrip.?, usu, usuNom,
    docuPric.?, docuNombre.?, docuNum.?, docuSiglas, docuAnio) <> (Vista1.tupled, Vista1.unapply)
}

class Vista2Table(tag: Tag) extends Table[Vista2](tag, "VISTA2") {
  def tramNum = column[String]("TRAM_NUM")
  def moviNum = column[Int]("MOVI_NUM")
  def moviOrigen = column[String]("MOVI_ORIGEN")
  def depeCod = column[String]("DEPE_COD")
  def moviDestino = column[String]("MOVI_DESTINO")
  def destCod = column[String]("DEST_COD")
  def moviFecEnv  = column[Timestamp]("MOVI_FEC_ENV")
  def moviFecIng = column[Timestamp]("MOVI_FEC_ING")
  def indiNombre = column[String]("INDI_NOMBRE")
  def indiCod = column[String]("INDI_COD")
  def moviObs = column[String]("MOVI_OBS")
  def estaNombre = column[String]("ESTA_NOMBRE")

  def * = (tramNum, moviNum, moviOrigen, depeCod, moviDestino, destCod, moviFecEnv.?, moviFecIng.?,
    indiNombre.?, indiCod.?, moviObs.?, estaNombre) <> (Vista2.tupled, Vista2.unapply)
}
