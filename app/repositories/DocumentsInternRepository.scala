package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{DocumentoInternoTable, DocumentosInternos}
import slick.lifted.TableQuery

@Singleton
class DocumentsInternRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
  extends BaseEntityRepository[DocumentoInternoTable, DocumentosInternos](dbConfigProvider, TableQuery[DocumentoInternoTable])

