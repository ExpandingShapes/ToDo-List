package utils

import org.bson.types.ObjectId
import org.joda.time.DateTime
import scala.util.Success
import reactivemongo.api.bson.{BSONDateTime, BSONValue}
import reactivemongo.bson.{BSONObjectID, DefaultBSONHandlers}
import reactivemongo.api.bson.BSONHandler
import scala.util.{Failure, Try}

trait CustomBSONHandlers extends DefaultBSONHandlers {
  implicit object BSONJodaDateTimeHandler
      extends reactivemongo.api.bson.BSONHandler[DateTime] {
    def writeTry(t: DateTime): Try[BSONValue] =
      Success(BSONDateTime(t.getMillis))
    def readTry(bson: BSONValue): Try[DateTime] =
      bson match {
        case BSONDateTime(l) => Try(new DateTime(l))
        case value =>
          Failure(
            new IllegalArgumentException(
              s"Expected BSONDateTime, but found $value"
            )
          )
      }
  }

  implicit object BSONObjectIdHandler extends BSONHandler[ObjectId] {
    override def writeTry(t: ObjectId): Try[BSONValue] =
      reactivemongo.api.bson.BSONObjectID.parse(t.toString)

    override def readTry(bson: BSONValue): Try[ObjectId] =
      bson match {
        case BSONObjectID(bytes) => Try(new ObjectId(bytes))
        case value =>
          Failure(
            new IllegalArgumentException(
              s"Expected BSONObjectId, but found $value"
            )
          )
      }
  }
}

object CustomBSONHandlers extends CustomBSONHandlers
