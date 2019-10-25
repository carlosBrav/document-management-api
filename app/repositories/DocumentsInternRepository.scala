package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{DocumentoInternoTable, DocumentosInternos}
import slick.lifted.TableQuery
import repositories.{MovimientosRepository}
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DocumentsInternRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                          movementRepository: MovimientosRepository)
  extends BaseEntityRepository[DocumentoInternoTable, DocumentosInternos](dbConfigProvider, TableQuery[DocumentoInternoTable]){

  def getMaxCorrelative(officeId: String, tipoDocuId: String) = {
    db.run(query.sortBy(_.numDocumento.desc).filter(x => x.dependenciaId === officeId && x.tipoDocuId === tipoDocuId).result.headOption)
  }

  def getDocumentsCirculars(userId: String) = {

    val documentTable = query
    val movementTable = movementRepository.query

    val typeDocuments = List("74545", "54545")

    val joinTables = for {
      (document, movements) <- documentTable.filter(x => x.tipoDocuId.inSet(typeDocuments) && x.userId === userId) joinLeft movementTable on (_.id === _.documentosInternosId)
    } yield(document, movements)

    db.run(joinTables.result)
  }
}

