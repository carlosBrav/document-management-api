package models

import java.sql.Timestamp

trait BaseEntity {
  val id: Option[String]
  val fechaCreacion: Option[Timestamp]
  val fechaModificacion: Option[Timestamp]
}

case class Rol(id: Option[String],
               nombre: String,
               fechaCreacion: Option[Timestamp] ,
               fechaModificacion: Option[Timestamp]
              ) extends BaseEntity

case class Usuario(id: Option[String],
                   usuario: String,
                   password: String,
                   estado: Boolean,
                   rolId: String,
                   nombre: String,
                   apellido: String,
                   telefono: Option[String],
                   fechaCreacion: Option[Timestamp] ,
                   fechaModificacion: Option[Timestamp]
                  ) extends BaseEntity

case class Dependencias(id: Option[String],
                        nombre: String,
                        estado: Boolean,
                        siglas: Option[String],
                        codigo: String,
                        fechaCreacion: Option[Timestamp] ,
                        fechaModificacion: Option[Timestamp]
                       ) extends BaseEntity

case class Movimientos(id: Option[String],
                       movimiento: Int,
                       numTram: String,
                       estadoDocumento: String,
                       estadoConfirmacion: Boolean,
                       documentosInternosId: Option[String],
                       dependenciasId: String,
                       dependenciasId1: String,
                       asignadoA: Option[String],
                       usuarioId: String,
                       fechaIngreso: Option[Timestamp],
                       fechaDerivacion: Option[Timestamp],
                       fechaEnvio: Option[Timestamp],
                       observacion: Option[String],
                       fechaCreacion: Option[Timestamp] ,
                       fechaModificacion: Option[Timestamp]
                      ) extends BaseEntity

case class TipoDocumento(id: Option[String],
                         nombreTipo: String,
                         flag1: Option[String],
                         flag2: Option[String],
                         fechaCreacion: Option[Timestamp] ,
                         fechaModificacion: Option[Timestamp]
                        ) extends BaseEntity

case class DocumentosInternos(id: Option[String],
                              fechaCreacion: Option[Timestamp],
                              fechaModificacion: Option[Timestamp],
                              estado: Boolean,
                              tipoDocuId: String,
                             ) extends BaseEntity