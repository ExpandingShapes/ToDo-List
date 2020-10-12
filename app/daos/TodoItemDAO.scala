package daos

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.compat.{legacyWriterNewValue, toDocumentWriter}
import models.TodoItem

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
      "$set" -> BSONDocument(
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

  def updateAll(isCompleted: Boolean): Future[UpdateWriteResult] = {
    collection.flatMap { c =>
      c.update.one(
        q = BSONDocument.empty,
        u =
          BSONDocument(f"$$set" -> BSONDocument("is_completed" -> isCompleted)),
        upsert = false,
        multi = true,
        collation = None,
        arrayFilters = Seq.empty
      )
    }
  }

  def delete(uuid: UUID): Future[Option[TodoItem]] =
    collection.flatMap(
      _.findAndRemove(selector = BSONDocument("uuid" -> uuid.toString))
        .map(_.result[TodoItem])
    )

  def deleteAll(): Future[WriteResult] =
    for {
      deleteBuilder <- collection.map(c => c.delete(ordered = false))
      result <- deleteBuilder.one(BSONDocument.empty, None, None)
    } yield result
}
