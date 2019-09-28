package services

import javax.inject.{Inject, Singleton}
import repositories.DocumentsInternRepository
import services.DocumentService
import models.{DocumentosInternos, DocumentoInternoTable}
import scala.concurrent.Future

@Singleton
class DocumentService @Inject()(
                                 override val repository: DocumentsInternRepository
                               )
  extends BaseEntityService[DocumentoInternoTable, DocumentosInternos, DocumentsInternRepository]
{
  def getMaxCorrelative(officeId: String, tipoDocuId: String): Future[DocumentosInternos] = {
    repository.getMaxCorrelative(officeId, tipoDocuId)
  }
}
