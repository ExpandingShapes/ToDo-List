package models

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import reactivemongo.api.bson._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import scala.util.Try

case class TodoItem(
    uuid: UUID = UUID.randomUUID(),
    name: String = "",
    isCompleted: Boolean = false,
    createdAt: DateTime = DateTime.now,
    updatedAt: DateTime = DateTime.now
)

object TodoItem {
  private implicit val dateTimeWriter: Writes[DateTime] =
    JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")
  private implicit val dateTimeJsReader: Reads[DateTime] =
    JodaReads.jodaDateReads("yyyyMMddHHmmss")

  implicit val todoItemOWrites: OWrites[TodoItem] = (
    (JsPath \ "uuid").write[UUID] and
      (JsPath \ "name").write[String] and
      (JsPath \ "is_completed").write[Boolean] and
      (JsPath \ "created_at").write[DateTime] and
      (JsPath \ "updated_at").write[DateTime]
  )(unlift(TodoItem.unapply))
  implicit val todoItemReads: Reads[TodoItem] = (
    (JsPath \ "uuid").read[UUID] and
      (JsPath \ "name").read[String] and
      (JsPath \ "is_completed").read[Boolean] and
      (JsPath \ "created_at").read[DateTime] and
      (JsPath \ "updated_at").read[DateTime]
  )(TodoItem.apply _)

  implicit object TodoItemReader extends BSONDocumentReader[TodoItem] {
    private val formatter: DateTimeFormatter =
      DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")

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
        formatter.parseDateTime(createdAt),
        formatter.parseDateTime(updatedAt)
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
