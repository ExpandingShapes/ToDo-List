package models

import java.time.{LocalDateTime, LocalTime}
import java.util.UUID

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsObject, JsPath, Json, OFormat, Reads, Writes}

case class TodoItem(
    uuid: UUID = UUID.randomUUID(),
    name: String = "",
    isCompleted: Boolean = false,
    createdAt: LocalDateTime = java.time.LocalDateTime.now(),
    updatedAt: LocalDateTime = java.time.LocalDateTime.now()
)

object TodoItem {
  implicit val todoItemWrites: Writes[TodoItem] = (
    (JsPath \ "uuid").write[UUID] and
      (JsPath \ "name").write[String] and
      (JsPath \ "is_completed")[Boolean] and
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
}
