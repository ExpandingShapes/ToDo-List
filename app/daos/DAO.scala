package daos

import java.util.UUID

import reactivemongo.api.commands.UpdateWriteResult
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

trait DAO[T] {
  def get(uuid: UUID): Option[T]
  def getAll: Future[List[BSONDocument]]
  def save(t: T): Future[UpdateWriteResult]
  def update(t: T): Future[UpdateWriteResult]
  def delete(t: T): Future[UpdateWriteResult]
}
