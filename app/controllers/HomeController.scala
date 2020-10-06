package controllers

import java.util.UUID

import javax.inject._
import models.TodoItem
import models.TodoItem.{todoItemOWrites, _}

import scala.concurrent.{ExecutionContext, Future}
import play.api.Logger
import play.api.mvc.{AbstractController, ControllerComponents, Request, _}
import play.modules.reactivemongo.{
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}
import reactivemongo.play.json._
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

  //POST
  def addTodoItem(): Action[JsValue] =
    Action.async(parse.json) { request: Request[JsValue] =>
      request.body
        .validate[TodoItem]
        .map { item =>
          todoItemService.createItem(item).map { _ =>
            Created
          }
        }
        .getOrElse(Future.successful(BadRequest("Received invalid JSON")))
    }

  def updateTodoItem(): Action[JsValue] =
    Action.async(parse.json) { request: Request[JsValue] =>
      request.body
        .validate[TodoItem]
        .map { item =>
          todoItemService.updateItem(item).map {
            case Some(value) => Ok(Json.toJson(item))
            case None        => NotFound
          }
        }
        .getOrElse(Future.successful(BadRequest("Received bad JSON")))

    }

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
