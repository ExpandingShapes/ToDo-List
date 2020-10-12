package models

import org.bson.types.ObjectId
import org.joda.time.DateTime
import reactivemongo.api.bson.Macros.Annotations.{Key => BsonKey}
import utils.{TodoItemBson, TodoItemJson}

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
