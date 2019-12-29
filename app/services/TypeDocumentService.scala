package services

import javax.inject.{Inject, Singleton}
import repositories.TypeDocumentRepository
import models.{TipoDocumento,TipoDocumentoTable}

@Singleton
class TypeDocumentService @Inject()(
                                     override val repository: TypeDocumentRepository
                                   )
  extends BaseEntityService[TipoDocumentoTable,TipoDocumento,TypeDocumentRepository]
{

}
