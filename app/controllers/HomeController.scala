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

  def getTodoItem(uuid: UUID): Action[AnyContent] =
    Action.async { implicit request: Request[AnyContent] =>
      {
        todoItemService.getItem(uuid).map {
          case Some(value) => Ok(Json.toJson(value))
          case None        => NotFound
        }
      }
    }

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

  def updateAllTodoItems() = NotImplemented

  def removeItem(uuid: UUID): Action[AnyContent] =
    Action.async {
      todoItemService.deleteItem(uuid).map {
        case Some(value) => Ok(Json.toJson(value))
        case None        => NotFound
      }
    }

  def removeAllTodoItems() = NotImplemented
}
