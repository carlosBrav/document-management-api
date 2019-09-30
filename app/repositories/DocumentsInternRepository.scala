package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{DocumentoInternoTable, DocumentosInternos}
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DocumentsInternRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
  extends BaseEntityRepository[DocumentoInternoTable, DocumentosInternos](dbConfigProvider, TableQuery[DocumentoInternoTable]){

  def getMaxCorrelative(officeId: String, tipoDocuId: String) = {
    filter(x => x.dependenciaId === officeId && x.tipoDocuId === tipoDocuId).map(x => x.maxBy(_.numDocumento))
  }
}

