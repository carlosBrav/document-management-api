package services

import models.{BaseEntity, BaseEntityTable}
import repositories.BaseEntityRepository

import scala.concurrent.Future
import scala.util.Try

trait BaseEntityService[T <: BaseEntityTable[E],E <: BaseEntity, R <: BaseEntityRepository[T,E]] {

  val repository: R

  def getById(id: String): Future[Option[E]] = repository.getById(id)

  def getAll: Future[Seq[E]] = repository.getAll

  def save(row: E): Future[Try[Int]] = repository.save(row)

  def saveList(row: Seq[E]): Future[Try[Option[Int]]] = repository.saveList(row)

  def deleteById(id: String): Future[Int] = repository.deleteById(id)

  def updateById(id: String, row: E): Future[Try[Int]] = repository.updateById(id,row)

}