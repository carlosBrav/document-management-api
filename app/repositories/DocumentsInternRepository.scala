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
    filter(x => x.dependenciaId === officeId && x.tipoDocuId === tipoDocuId).map(x => x.maxBy(_.numDocumento))
  }

  def getDocumentsCirculars(typeDocumentId: String) = {

    val documentTable = query
    val movementTable = movementRepository.query

    val joinTables = for {
      (document, movements) <- documentTable.filter(x => x.tipoDocuId === typeDocumentId) joinLeft movementTable on (_.id === _.documentosInternosId)
    } yield(document, movements)

    db.run(joinTables.result)
  }
}

