package daos

import scala.concurrent.Future
import reactivemongo.api.commands.WriteResult

trait DAO[T] {
  def getById(id: String): Future[Option[T]]
  def getAll: Future[Seq[T]]
  def create(t: T): Future[WriteResult]
  def update(t: T): Future[Option[T]]
  def updateAll(i: Boolean): Future[WriteResult]
  def delete(id: String): Future[Option[T]]
  def deleteAllCompleted(): Future[WriteResult]
}
