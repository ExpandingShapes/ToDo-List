package services

import javax.inject.Inject
import scala.concurrent.Future
import daos.TodoItemDAO
import models.TodoItem
import reactivemongo.api.commands.WriteResult

trait ITodoItemService {
  def getItem(id: String): Future[Option[TodoItem]]
  def getAllItems: Future[Seq[TodoItem]]
  def createItem(todolistItem: TodoItem): Future[WriteResult]
  def updateItem(id: String, newIsCompleted: Boolean): Future[Option[TodoItem]]
  def deleteItem(id: String): Future[Option[TodoItem]]
  def deleteAllCompletedItems(): Future[WriteResult]
}

class TodoItemService @Inject() (todoItemDAO: TodoItemDAO)
    extends ITodoItemService {
  def getItem(id: String): Future[Option[TodoItem]] = todoItemDAO.getById(id)
  def getAllItems: Future[Seq[TodoItem]] = todoItemDAO.getAll
  def createItem(todoItem: TodoItem): Future[WriteResult] =
    todoItemDAO.create(todoItem)
  def updateItem(
      id: String,
      newIsCompleted: Boolean
  ): Future[Option[TodoItem]] =
    todoItemDAO.update(id, newIsCompleted)
  def updateAllItems(isCompleted: Boolean): Future[WriteResult] =
    todoItemDAO.updateAll(isCompleted)
  def deleteItem(id: String): Future[Option[TodoItem]] =
    todoItemDAO.delete(id)
  def deleteAllCompletedItems(): Future[WriteResult] =
    todoItemDAO.deleteAllCompleted()

}
