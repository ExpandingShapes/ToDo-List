package controllers

import java.util.UUID
import javax.inject._
import models.TodoItem._
import scala.concurrent.{ExecutionContext, Future}
import play.api.Logger
import play.api.mvc.{AbstractController, ControllerComponents}
import play.modules.reactivemongo.{
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}
import reactivemongo.play.json._
import play.api.mvc._
import play.api.libs.json._
import services.TodoItemService

@Singleton
class HomeController @Inject() (
    override val controllerComponents: ControllerComponents,
    val reactiveMongoApi: ReactiveMongoApi,
    todoItemService: TodoItemService
) extends AbstractController(controllerComponents)
    with MongoController
    with ReactiveMongoComponents {

  val logger: Logger = Logger(this.getClass)
  implicit def ec: ExecutionContext = controllerComponents.executionContext

  def getAllTodoItems: Action[AnyContent] =
    Action.async {
      todoItemService.getAllItems.map { items =>
        Ok(Json.toJson(items))
      }
    }

  def getTodoItem: Action[AnyContent] =
    Action.async { implicit request: Request[AnyContent] =>
      {
        val jsonBody = request.body.asJson
        jsonBody match {
          case Some(value) =>
            val uuid = (value \ "uuid").as[UUID]
            todoItemService.getItem(uuid).map { item =>
              item
                .map { i =>
                  Ok(Json.toJson(i))
                }
                .getOrElse(NotFound)
            }
          case None => Future.successful(BadRequest)
        }
      }
    }
//          .getOrElse(BadRequest("Your JSON is bad!")) match {
//          case Some(value) =>
//            val uuid = UUID.fromString(value.toString)
//            logger.info(uuid.toString)
//            todoItemService.getItem(uuid).map { item =>
//              item
//                .map { i =>
//                  Ok(Json.toJson(i))
//                }
//                .getOrElse(NotFound)
//            }
//          case None => Future.successful(BadRequest)
//        }

//      }

//    }
//  Action.async { implicit request: Request[AnyContent] =>
//    {
//      todoItemService.getAllItems.map { items =>
//        (items.reverse)
//      }
//    }
  //}

  //POST
  def addTodoItem() = Action { Ok("") }
//    Action.async(parse.json) { implicit request: Request[JsValue] =>
//      request.body
//        .validate[TodoItem]
//        .map { item =>
//          collection
//            .flatMap(_.insert.one(item))
//            .map { lastError =>
//              logger.debug(s"Successfully inserted with LastError: $lastError")
//              Created(
//                Json.obj("uuid" -> item.uuid.toString)
//              )
//            }
//        }
//        .getOrElse(Future.successful(BadRequest("invalid json")))
//    }

  //DELETE
  def removeItem(): Status = NotImplemented
//  Action.async(parse.json) { implicit request: Request[JsValue] =>
//    {
//      //TODO: match validate result
//      val uuid: String = request.body.validate[String] //.getOrElse("")
//      val selector = Json.obj("uuid" -> uuid)
//      val updatedField = Json.obj("isDeleted" -> true)
//      collection
//        .flatMap(
//          _.update.one(selector, updatedField, upsert = false, multi = false)
//        )
//        .onComplete {
//          case Failure(exception) =>
//            logger.debug(exception.toString)
//            InternalServerError
//          case Success(value) => NoContent
//        }
//    }
//  }

}
