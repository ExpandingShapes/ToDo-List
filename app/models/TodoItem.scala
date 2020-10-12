package models

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
import org.bson.types.ObjectId
import play.api.libs.json._
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import reactivemongo.api.bson._

import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import play.api.libs.json.JsonNaming.SnakeCase
import reactivemongo.api.bson.Macros.Annotations.{Key => BsonKey}
import utils.{TodoItemBson, TodoItemJson}

import scala.util.Try

case class TodoItem(
    _id: ObjectId,
    name: String = "",
    @BsonKey("is_completed") isCompleted: Boolean = false,
    created: DateTime = DateTime.now,
    updated: DateTime = DateTime.now
) {
  val id: String = _id.toString
}

object TodoItem extends TodoItemJson with TodoItemBson
//  private implicit val dateTimeWriter: Writes[DateTime] =
//    JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")
//  private implicit val dateTimeJsReader: Reads[DateTime] =
//    JodaReads.jodaDateReads("yyyyMMddHHmmss")
//
//  implicit val todoItemOWrites: OWrites[TodoItem] = (
//    (JsPath \ "id").write[String] and
//      (JsPath \ "name").write[String] and
//      (JsPath \ "is_completed").write[Boolean] and
//      (JsPath \ "created").write[DateTime] and
//      (JsPath \ "updated").write[DateTime]
//  )(unlift(TodoItem.unapply))
//  implicit val todoItemReads: Reads[TodoItem] = (
//    (JsPath \ "id").read[String].map(new ObjectId(_)) and
//      (JsPath \ "name").read[String] and
//      (JsPath \ "is_completed").read[Boolean] and
//      (JsPath \ "created").read[DateTime] and
//      (JsPath \ "updated").read[DateTime]
//  )(TodoItem.apply _)
//
//  implicit object TodoItemReader extends BSONDocumentReader[TodoItem] {
//    private val formatter: DateTimeFormatter =
//      DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")
//
//    def readDocument(bson: BSONDocument): Try[TodoItem] = {
//      for {
//        id <- bson.getAsTry[String]("id")
//        name <- bson.getAsTry[String]("name")
//        isCompleted <- bson.getAsTry[Boolean]("is_completed")
//        created <- bson.getAsTry[String]("created")
//        updated <- bson.getAsTry[String]("updated")
//      } yield TodoItem(
//        id.fromString(id),
//        name,
//        isCompleted,
//        formatter.parseDateTime(created),
//        formatter.parseDateTime(updated)
//      )
//    }
//  }
//
//  implicit val writeDocument: BSONDocumentWriter[TodoItem] =
//    BSONDocumentWriter[TodoItem] { item =>
//      BSONDocument(
//        "id" -> item.id.toString,
//        "name" -> item.name,
//        "is_completed" -> item.isCompleted,
//        "created" -> item.created.toString,
//        "updated" -> item.updated.toString
//      )
//    }
//}
