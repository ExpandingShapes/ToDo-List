package models

import java.util.UUID
import play.api.libs.json.{OFormat, Json}

case class TodolistItem(
    uuid: UUID = UUID.randomUUID(),
    description: String = "",
    isCompleted: Boolean = false,
    isDeleted: Boolean = false
)

object TodolistItem {
  implicit val todolistItemFormat: OFormat[TodolistItem] =
    Json.using[Json.WithDefaultValues].format[TodolistItem]
}
