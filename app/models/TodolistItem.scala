package models

import play.api.libs.json.OFormat

case class TodolistItem(
    description: String = "",
    isCompleted: Boolean = false,
    isDeleted: Boolean = false
)

object TodolistItem {
  import play.api.libs.json.Json

  implicit val todolistItemFormat: OFormat[TodolistItem] =
    Json.format[TodolistItem]
}
