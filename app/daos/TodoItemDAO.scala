package daos

import java.util.UUID
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.Json
import reactivemongo.api.{Cursor, MongoConnection, ReadPreference}
import reactivemongo.play.json.collection.Helpers.idWrites
import scala.concurrent.ExecutionContext.Implicits.global
import models.TodoItem
import reactivemongo.api.commands.UpdateWriteResult
import reactivemongo.play.json.collection.JSONCollection
import scala.concurrent.Future

class TodoItemDAO extends DAO[TodoItem] {
  private val driver = new reactivemongo.api.AsyncDriver
  private val uri = "mongodb://admin:admin@127.0.0.1:27017/Mongo-Exercises"
  private def connection: Future[MongoConnection] =
    for {
      parsedUri <- MongoConnection.fromString(uri)
      connection <- driver.connect(parsedUri)
    } yield connection

  private def collection: Future[JSONCollection] = {
    connection
      .flatMap(_.database("Mongo-Exercises"))
      .map(_.collection[JSONCollection]("todo-list-items"))
  }

  def getAll: Future[List[TodoItem]] =
    collection
      .map {
        _.find(
          Json.obj(),
          projection = Option.empty[TodoItem]
        ).cursor[TodoItem](ReadPreference.primary)
      }
      .flatMap(
        _.collect[List](-1, Cursor.FailOnError[List[TodoItem]]())
      )
  def get(uuid: UUID): Option[TodoItem] = ???
  def save(t: TodoItem): Future[UpdateWriteResult] = ???
  def update(t: TodoItem): Future[UpdateWriteResult] = ???
  def delete(t: TodoItem): Future[UpdateWriteResult] = ???

}
