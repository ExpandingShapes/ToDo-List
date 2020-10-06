package daos

import java.util.UUID
import javax.inject.Inject
import reactivemongo.api.Cursor
import models.TodoItem
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.bson.BSONDocument
import scala.concurrent.{ExecutionContext, Future}

class TodoItemDAO @Inject() (implicit
    ec: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) extends DAO[TodoItem] {

  private def collection: Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection("todo-list-items"))

  def getAll: Future[Seq[TodoItem]] = {
    collection
      .flatMap(
        _.find(BSONDocument.empty, Option.empty[BSONDocument])
          .cursor[TodoItem]()
          .collect[Seq](-1, Cursor.FailOnError[Seq[TodoItem]]())
      )
  }

  def getById(uuid: UUID): Future[Option[TodoItem]] =
    collection.flatMap(
      _.find(BSONDocument("uuid" -> uuid.toString), Option.empty[BSONDocument])
        .one[TodoItem]
    )

  def create(t: TodoItem): Future[WriteResult] =
    collection.flatMap(_.insert.one(t.copy()))

  def update(t: TodoItem): Future[Option[TodoItem]] = {
    val updateModifier = BSONDocument(
      f"$$set" -> BSONDocument(
        "uuid" -> t.uuid.toString,
        "name" -> t.name,
        "is_completed" -> t.isCompleted,
        "created_at" -> t.createdAt.toString,
        "updated_at" -> t.updatedAt.toString
      )
    )

    collection
      .flatMap(
        _.findAndUpdate(
          BSONDocument("uuid" -> t.uuid.toString),
          updateModifier,
          fetchNewObject = true
        )
      )
      .map(_.result[TodoItem])
  }
  def delete(uuid: UUID): Future[Option[TodoItem]] =
    collection.flatMap(
      _.findAndRemove(selector = BSONDocument("uuid" -> uuid.toString))
        .map(_.result[TodoItem])
    )
}
