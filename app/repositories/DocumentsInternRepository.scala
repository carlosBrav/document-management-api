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
    if(tipoDocuId == "84545"){
      db.run(query.sortBy(_.numDocumento.desc).filter(x=>x.tipoDocuId === tipoDocuId).result.headOption)
    }
    else db.run(query.sortBy(_.numDocumento.desc).filter(x => x.origenId === officeId && x.tipoDocuId === tipoDocuId).result.headOption)
  }

  def loadByDocumentId(documentId: String) = {
    filter(x => x.id === documentId).map(documents => Some(documents.head)) recover { case _: Exception => None }
  }

  def deleteDocuments(documentsId: Seq[String], previousMovementId: Seq[String]) = {

    val docIntQuery = query
    val moveQuery = movementRepository.query


    db.run(
      docIntQuery.filter(x => x.id.inSet(documentsId)).delete
        andThen moveQuery.filter(x=>x.documentosInternosId.inSet(documentsId)).delete
        andThen moveQuery.filter(x=>x.id.inSet(previousMovementId)).map(x=>x.estadoDocumento).update("EN PROCESO")
    )
  }
}

