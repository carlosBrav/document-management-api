package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{DocumentoInternoTable, DocumentosInternos, UsuarioTable}
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DocumentsInternRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                                          movementRepository: MovimientosRepository,
                                          dependencyRepository: DependencyRepository,
                                          userRepository: UserRepository,
                                          typeDocumentRepository: TypeDocumentRepository)
  extends BaseEntityRepository[DocumentoInternoTable, DocumentosInternos](dbConfigProvider, TableQuery[DocumentoInternoTable]){

  def getMaxCorrelative(officeId: String, tipoDocuId: String) = {
    db.run(query.sortBy(_.numDocumento.desc).filter(x => x.dependenciaId === officeId && x.tipoDocuId === tipoDocuId).result.headOption)
  }

  def loadByDocumentId(documentId: String) = {
    filter(x => x.id === documentId && x.active === true).map(documents => Some(documents.head)) recover { case _: Exception => None }
  }
}

