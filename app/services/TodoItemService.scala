package services

import java.util.UUID
import daos.TodoItemDAO
import models.TodoItem
import scala.concurrent.Future

trait ITodoItemService {
  def getItem(uuid: UUID): Option[TodoItem]
  def getAllItems: Future[List[TodoItem]]
  def saveItem(todolistItem: TodoItem): Int
  def updateItem(todolistItem: TodoItem): Int
  def deleteItem(uuid: UUID): Int
}

class TodoItemService extends ITodoItemService {
  val todolistItemDAO: TodoItemDAO = new TodoItemDAO

  def getItem(uuid: UUID): Option[TodoItem] = ???
  def getAllItems: Future[List[TodoItem]] = todolistItemDAO.getAll
  def saveItem(todolistItem: TodoItem): Int = ???
  def updateItem(todolistItem: TodoItem): Int = ???
  def deleteItem(uuid: UUID): Int = ???
}
