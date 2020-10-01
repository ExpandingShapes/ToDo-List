package controllers

import javax.inject._
import models.TodolistItem
import reactivemongo.api.ReadPreference
import reactivemongo.play.json.collection.JSONCollection
import scala.concurrent.{ExecutionContext, Future}
import play.api.Logger
import play.api.mvc.{AbstractController, ControllerComponents}
import play.modules.reactivemongo.{
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}
import reactivemongo.api.Cursor
import reactivemongo.play.json._
import collection._
import play.api.mvc._
import play.api.libs.json._
import scala.util.{Failure, Success}

@Singleton
class HomeController @Inject() (
    override val controllerComponents: ControllerComponents,
    val reactiveMongoApi: ReactiveMongoApi
) extends AbstractController(controllerComponents)
    with MongoController
    with ReactiveMongoComponents {

  val logger: Logger = Logger(this.getClass)
  implicit def ec: ExecutionContext = controllerComponents.executionContext
  def collection: Future[JSONCollection] =
    database.map(_.collection[JSONCollection]("todo-list-items"))

  //GET
  def index(): Action[AnyContent] =
    Action.async { implicit request: Request[AnyContent] =>
      {
        // get a cursor over not deleted items from MongoDB
        val cursor: Future[Cursor[TodolistItem]] = collection
          .map {
            _.find(
              Json.obj("isDeleted" -> false),
              projection = Option.empty[TodolistItem]
            ).cursor[TodolistItem](ReadPreference.primary)
          }

        // gather JsObjects from the db in a list
        val futurePersonsList: Future[List[TodolistItem]] =
          cursor.flatMap(
            _.collect[List](-1, Cursor.FailOnError[List[TodolistItem]]())
          )

        // pass the list of items
        futurePersonsList.map { items =>
          Ok(views.html.index(items.reverse))
        }
      }
    }

  //POST
  def addItem(): Action[JsValue] =
    Action.async(parse.json) { implicit request: Request[JsValue] =>
      request.body
        .validate[TodolistItem]
        .map { item =>
          collection.flatMap(_.insert.one(item)).map { lastError =>
            logger.debug(s"Successfully inserted with LastError: $lastError")
            Created(
              Json.obj("uuid" -> item.uuid.toString)
            )
          }
        }
        .getOrElse(Future.successful(BadRequest("invalid json")))
    }

  //DELETE
  def removeItem(): Unit =
    Action.async(parse.json) { implicit request: Request[JsValue] =>
      {
        //TODO: match validate result
        val uuid: String = request.body.validate[String].getOrElse("")
        val selector = Json.obj("uuid" -> uuid)
        val updatedField = Json.obj("isDeleted" -> true)
        collection
          .flatMap(
            _.update.one(selector, updatedField, upsert = false, multi = false)
          )
          .onComplete {
            case Failure(exception) =>
              logger.debug(exception.toString)
              InternalServerError
            case Success(value) => NoContent
          }
      }
    }

}
