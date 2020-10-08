package models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsPath, OWrites, Reads}
import reactivemongo.api.bson._
//import reactivemongo.bson.Macros
import scala.util.Try

case class TodoItem(
    uuid: UUID = UUID.randomUUID(),
    name: String = "",
    isCompleted: Boolean = false,
    createdAt: LocalDateTime = java.time.LocalDateTime.now(),
    updatedAt: LocalDateTime = java.time.LocalDateTime.now()
)

object TodoItem {
  implicit val todoItemOWrites: OWrites[TodoItem] = (
    (JsPath \ "uuid").write[UUID] and
      (JsPath \ "name").write[String] and
      (JsPath \ "is_completed").write[Boolean] and
      (JsPath \ "created_at").write[LocalDateTime] and
      (JsPath \ "updated_at").write[LocalDateTime]
  )(unlift(TodoItem.unapply))
  implicit val todoItemReads: Reads[TodoItem] = (
    (JsPath \ "uuid").read[UUID] and
      (JsPath \ "name").read[String] and
      (JsPath \ "is_completed").read[Boolean] and
      (JsPath \ "created_at").read[LocalDateTime] and
      (JsPath \ "updated_at").read[LocalDateTime]
  )(TodoItem.apply _)

  implicit object TodoItemReader extends BSONDocumentReader[TodoItem] {
    def readDocument(bson: BSONDocument): Try[TodoItem] = {
      for {
        uuid <- bson.getAsTry[String]("uuid")
        name <- bson.getAsTry[String]("name")
        isCompleted <- bson.getAsTry[Boolean]("is_completed")
        createdAt <- bson.getAsTry[String]("created_at")
        updatedAt <- bson.getAsTry[String]("updated_at")
      } yield TodoItem(
        UUID.fromString(uuid),
        name,
        isCompleted,
        LocalDateTime
          .parse(createdAt.toString, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        LocalDateTime
          .parse(updatedAt.toString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
      )
    }
  }

  implicit val writeDocument: BSONDocumentWriter[TodoItem] =
    BSONDocumentWriter[TodoItem] { item =>
      BSONDocument(
        "uuid" -> item.uuid.toString,
        "name" -> item.name,
        "is_completed" -> item.isCompleted,
        "created_at" -> item.createdAt.toString,
        "updated_at" -> item.updatedAt.toString
      )
    }
}
