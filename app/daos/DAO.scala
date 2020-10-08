package daos

import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import java.util.UUID

import scala.concurrent.Future

trait DAO[T] {
  def getById(uuid: UUID): Future[Option[T]]
  def getAll: Future[Seq[T]]
  def create(t: T): Future[WriteResult]
  def update(t: T): Future[Option[T]]
  def updateAll(i: Boolean): Future[UpdateWriteResult]
  //TODO: add updateAll method
  def delete(uuid: UUID): Future[Option[T]]
}
