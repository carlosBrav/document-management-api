package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{TipoDocumento, TipoDocumentoTable}
import slick.lifted.TableQuery

@Singleton
class TypeDocumentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
  extends BaseEntityRepository[TipoDocumentoTable,TipoDocumento](dbConfigProvider, TableQuery[TipoDocumentoTable]){

}
