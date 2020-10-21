package daos

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.bson.{BSONDocument, BSONObjectID}
import models.TodoItem
import org.joda.time.DateTime

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

  def getById(id: String): Future[Option[TodoItem]] =
    collection.flatMap(
      _.find(BSONDocument("id" -> id), Option.empty[BSONDocument])
        .one[TodoItem]
    )

  def create(t: TodoItem): Future[WriteResult] =
    collection.flatMap(_.insert.one(t.copy()))

  def update(t: TodoItem): Future[Option[TodoItem]] = {
    val updateModifier = BSONDocument(
      "$set" -> BSONDocument(
        "is_completed" -> t.isCompleted,
        "created" -> t.created.toString,
        "updated" -> DateTime.now.toString
      )
    )

    collection
      .flatMap(
        _.findAndUpdate(
          BSONDocument("id" -> t.id),
          updateModifier,
          fetchNewObject = true
        )
      )
      .map(_.result[TodoItem])
  }

  def updateAll(isCompleted: Boolean): Future[WriteResult] = {
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

  def delete(id: String): Future[Option[TodoItem]] =
    collection.flatMap(
      _.findAndRemove(selector = BSONDocument("id" -> id))
        .map(_.result[TodoItem])
    )

  def deleteAllCompleted(): Future[WriteResult] =
    for {
      deleteBuilder <- collection.map(c => c.delete(ordered = false))
      result <-
        deleteBuilder.one(BSONDocument("is_completed" -> true), None, None)
    } yield result
}
