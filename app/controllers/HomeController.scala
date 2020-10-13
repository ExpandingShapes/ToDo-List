package controllers

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import play.api.Logging
import play.api.mvc._
import services.TodoItemService
import models.TodoItem

@Singleton
class HomeController @Inject() (
    override val controllerComponents: ControllerComponents,
    todoItemService: TodoItemService
) extends AbstractController(controllerComponents)
    with Logging {

  implicit def ec: ExecutionContext = controllerComponents.executionContext

  def getAllTodoItems: Action[AnyContent] =
    Action.async {
      todoItemService.getAllItems.map { items =>
        Ok(Json.toJson(items))
      }
    }

  def getTodoItem(id: String): Action[AnyContent] =
    Action.async { implicit request: Request[AnyContent] =>
      {
        todoItemService.getItem(id).map {
          case Some(value) => Ok(Json.toJson(value))
          case None =>
            logger.debug(s"todoItem with id = $id not found")
            NotFound
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
        .getOrElse {
          logger.debug(
            "Failed to create a new todoItem from JSON: \n $request.body"
          )
          Future.successful(BadRequest("Received invalid JSON"))
        }
    }

  def updateTodoItem(): Action[JsValue] =
    Action.async(parse.json) { request: Request[JsValue] =>
      request.body
        .validate[TodoItem]
        .map { item =>
          todoItemService.updateItem(item).map {
            case Some(value) => Ok(Json.toJson(item))
            case None =>
              logger.debug(
                s"Failed to update a todoItem because no todoItem with id = ${item.id} found."
              )
              NotFound
          }
        }
        .getOrElse {
          logger.debug(
            s"Failed to create a new todoItem from JSON: \n $request.body"
          )
          Future.successful(BadRequest("Received bad JSON"))
        }
    }

  def updateAllTodoItems(): Action[JsValue] =
    Action.async(parse.json) { request =>
      {
        val isCompleted: Boolean = (request.body \ "is_completed").as[Boolean]
        todoItemService.updateAllItems(isCompleted).map { _ =>
          NoContent
        }
      }
    }

  def removeTodoItem(id: String): Action[AnyContent] =
    Action.async {
      todoItemService.deleteItem(id).map {
        case Some(value) => Ok(Json.toJson(value))
        case None =>
          logger.debug(
            s"Failed to delete a todoItem, no todoItem with id = $id found."
          )
          NotFound
      }
    }

  def removeAllTodoItems(): Action[AnyContent] =
    Action.async {
      todoItemService.deleteAllItems().map { _ =>
        NoContent
      }
    }
}
