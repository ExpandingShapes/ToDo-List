package models

import org.bson.types.ObjectId
import org.joda.time.DateTime
import play.api.libs.json._
import reactivemongo.api.bson.{
  BSONDocumentHandler,
  FieldNaming,
  MacroConfiguration,
  MacroOptions,
  Macros
}
import reactivemongo.api.bson.Macros.Annotations.{Key => BsonKey}
import utils.CustomBSONHandlers

case class TodoItem(
    _id: ObjectId = new ObjectId,
    name: String = "",
    @BsonKey("is_completed") isCompleted: Boolean = false,
    created: DateTime = DateTime.now,
    updated: DateTime = DateTime.now,
    var id: String = ""
) { id = _id.toString }

trait TodoItemJson {
  implicit val oidReads =
    Reads[ObjectId](js => js.validate[String].map(new ObjectId(_)))
  implicit val oidWrites: Writes[ObjectId] = (oid: ObjectId) => {
    Json.obj(
      "$oid" -> oid.toString
    )
  }
  implicit val jodaDateReads = JodaReads.jodaDateReads("yyyy-MM-dd HH:mm:ss")
  implicit val jodaDateWrites: Writes[DateTime] = (d: DateTime) =>
    JsString(d.toString())
  implicit def jsonFormat: OFormat[TodoItem] =
    Json.using[Json.WithDefaultValues].format[TodoItem]
}

trait TodoItemBson extends CustomBSONHandlers {
  implicit val bsonHandler: BSONDocumentHandler[TodoItem] = {
    implicit def cfg: MacroConfiguration =
      MacroConfiguration(
        fieldNaming = FieldNaming.SnakeCase
      )

    Macros.using[MacroOptions.Default].handler[TodoItem]
  }
}

object TodoItem extends TodoItemJson with TodoItemBson
