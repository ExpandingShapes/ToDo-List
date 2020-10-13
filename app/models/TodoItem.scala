package models

import org.bson.types.ObjectId
import org.joda.time.DateTime
import play.api.libs.json.JodaReads._
import play.api.libs.json._
import play.api.libs.json.{Reads, Json, JsonConfiguration, OWrites}
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
  val oidReads = Reads[ObjectId](js => js.validate[String].map(new ObjectId(_)))
  implicit val jodaDateWrites: Writes[DateTime] = new Writes[DateTime] {
    def writes(d: DateTime): JsValue = JsString(d.toString())
  }

  implicit val writes: Writes[TodoItem] = (o: TodoItem) =>
    Json.obj(
      "id" -> o.id,
      "name" -> o.name,
      "is_completed" -> o.isCompleted,
      "created" -> Json.toJson(o.created),
      "updated" -> Json.toJson(o.updated)
    )

  implicit val reads: Reads[TodoItem] = (
    (__ \ "id").read[ObjectId](oidReads) and
      (__ \ "name").read[String] and
      (__ \ "is_completed").read[Boolean] and
      (__ \ "created").read[DateTime] and
      (__ \ "updated").read[DateTime]
  )(TodoItem.apply _)
}

trait TodoItemBson extends CustomBSONHandlers {
  implicit val config = JsonConfiguration(SnakeCase)
  implicit val bsonHandler: BSONDocumentHandler[TodoItem] =
    Macros.handler[TodoItem]
}

object TodoItem extends TodoItemJson with TodoItemBson
