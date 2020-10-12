package utils

import models.TodoItem
import reactivemongo.api.bson.{BSONDocumentHandler, Macros}
//import play.api.libs.json.JodaWrites._
//import play.api.libs.json.JodaReads._

trait TodoItemBson extends CustomBSONHandlers {
  implicit val bsonHandler: BSONDocumentHandler[TodoItem] =
    Macros.handler[TodoItem]
}

object TodoItemBson extends TodoItemBson
