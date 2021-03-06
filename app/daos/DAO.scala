package daos

import scala.concurrent.Future
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}

trait DAO[T] {
  def getById(id: String): Future[Option[T]]
  def getAll: Future[Seq[T]]
  def create(t: T): Future[WriteResult]
  def update(t: T): Future[Option[T]]
  def updateAll(i: Boolean): Future[UpdateWriteResult]
  def delete(id: String): Future[Option[T]]
  def deleteAll(): Future[WriteResult]
}
