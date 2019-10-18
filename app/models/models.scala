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
                       movimiento: Option[Int],
                       numTram: Option[String],
                       estadoDocumento: String,
                       documentosInternosId: Option[String],
                       dependenciasId: String,
                       dependenciasId1: String,
                       asignadoA: Option[String],
                       usuarioId: String,
                       fechaIngreso: Option[Timestamp],
                       fechaEnvio: Option[Timestamp],
                       observacion: Option[String],
                       indiNombre: Option[String],
                       indiCod: Option[String],
                       docuNombre: Option[String],
                       docuNum: Option[String],
                       docuSiglas: Option[String],
                       docuAnio: Option[String],
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
                              estadoDocumento: Option[String],
                              tipoDocuId: String,
                              numDocumento: Option[Int],
                              siglas: Option[String],
                              anio: Option[String],
                              asunto: Option[String],
                              observacion: Option[String],
                              dependenciaId: String,
                              active: Boolean,
                              fechaCreacion: Option[Timestamp],
                              fechaModificacion: Option[Timestamp],
                             ) extends BaseEntity


case class Vista1(tramNum: String,
                  tramFecha: Option[Timestamp],
                  depeOrigen: String,
                  depeCod: String,
                  tramObs: Option[String],
                  estaDescrip: Option[String],
                  usu: String,
                  usuNom: String,
                  docuPric: Option[String],
                  docuNombre: Option[String],
                  docuNum: Option[String],
                  docuSiglas: String,
                  docuAnio: String)

case class Vista2(tramNum: String,
                  moviNum: Int,
                  moviOrigen: String,
                  depeCod: String,
                  moviDestino: String,
                  destCod: String,
                  moviFecEnv: Option[Timestamp],
                  moviFecIng: Option[Timestamp],
                  indiNombre: Option[String],
                  indiCod: Option[String],
                  moviObs: Option[String],
                  estaNombre: String)