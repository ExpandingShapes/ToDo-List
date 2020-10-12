package models

import org.bson.types.ObjectId
import org.joda.time.DateTime
import play.api.libs.json.JodaReads._
import play.api.libs.json._
import play.api.libs.json.{Reads, JsPath, Json, JsonConfiguration, OWrites}
import play.api.libs.json.JsonNaming.SnakeCase
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import reactivemongo.api.bson.{BSONDocumentHandler, Macros}
import reactivemongo.api.bson.Macros.Annotations.{Key => BsonKey}
import utils.CustomBSONHandlers

case class TodoItem(
    _id: ObjectId,
    name: String = "",
    @BsonKey("is_completed") isCompleted: Boolean = false,
    created: DateTime = DateTime.now,
    updated: DateTime = DateTime.now
) {
  val id: String = _id.toString
}

trait TodoItemJson {
  implicit val todoItemWrites: Writes[TodoItem] = (o: TodoItem) => {
    Json.obj(
      "id" -> o.id,
      "name" -> o.name,
      "is_completed" -> o.isCompleted.toString,
      "created" -> o.created.toString,
      "updated" -> o.updated.toString
    )
  }

  implicit val todoItemReads: Reads[TodoItem] = (
    (JsPath \ "id").read[String].map(new ObjectId(_)) and
      (JsPath \ "name").read[String] and
      (JsPath \ "is_completed").read[Boolean] and
      (JsPath \ "created").read[DateTime] and
      (JsPath \ "updated").read[DateTime]
  )(TodoItem.apply _)
}

trait TodoItemBson extends CustomBSONHandlers {
  implicit val config = JsonConfiguration(SnakeCase)
  implicit val bsonHandler: BSONDocumentHandler[TodoItem] =
    Macros.handler[TodoItem]
}

object TodoItem extends TodoItemJson with TodoItemBson
