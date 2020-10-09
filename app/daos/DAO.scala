package daos

import java.util.UUID
import scala.concurrent.Future
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}

trait DAO[T] {
  def getById(uuid: UUID): Future[Option[T]]
  def getAll: Future[Seq[T]]
  def create(t: T): Future[WriteResult]
  def update(t: T): Future[Option[T]]
  def updateAll(i: Boolean): Future[UpdateWriteResult]
  def delete(uuid: UUID): Future[Option[T]]
  def deleteAll(): Future[WriteResult]
}
