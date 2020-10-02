package daos

import java.util.UUID

import reactivemongo.api.{Cursor, MongoConnection}

import scala.concurrent.ExecutionContext.Implicits.global
import models.TodoItem
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.compat.toDocumentWriter
import reactivemongo.api.commands.UpdateWriteResult
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

class TodoItemDAO extends DAO[TodoItem] {
  private val driver = new reactivemongo.api.AsyncDriver
  private val uri = "mongodb://admin:admin@127.0.0.1:27017/Mongo-Exercises"
  private def connection: Future[MongoConnection] =
    for {
      parsedUri <- MongoConnection.fromString(uri)
      connection <- driver.connect(parsedUri)
    } yield connection

  private def collection: Future[BSONCollection] =
    connection
      .flatMap(_.database("Mongo-Exercises"))
      .map(_.collection[BSONCollection]("todo-list-items"))

  def getAll: Future[List[BSONDocument]] = {
    collection
      .flatMap(
        _.find(BSONDocument())
          .cursor[BSONDocument]()
          .collect[List](-1, Cursor.FailOnError[List[BSONDocument]]())
      )
  }

  def get(uuid: UUID): Option[TodoItem] = ???
  def save(t: TodoItem): Future[UpdateWriteResult] = ???
  def update(t: TodoItem): Future[UpdateWriteResult] = ???
  def delete(t: TodoItem): Future[UpdateWriteResult] = ???

}
