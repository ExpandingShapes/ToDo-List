package utils

import models.TodoItem
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.JsonConfiguration.Aux
import play.api.libs.json._
import play.api.libs.json.JsonNaming.SnakeCase
//import play.api.libs.json.JodaWrites._
//import play.api.libs.json.JodaReads._

trait TodoItemJson {
  implicit val config: Aux[Json.MacroOptions] = JsonConfiguration(SnakeCase)
  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

  val jodaDateReads = Reads[DateTime](js =>
    js.validate[String]
      .map[DateTime](dtString =>
        DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
      )
  )
  val jodaDateWrites = new Writes[DateTime] {
    def writes(d: DateTime): JsValue = JsString(d.toString())
  }
  implicit val writes: OWrites[TodoItem] = Json.writes[TodoItem]
  implicit val reads: Reads[TodoItem] = Json.reads[TodoItem]
}
