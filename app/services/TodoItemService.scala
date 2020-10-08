package services

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future
import daos.TodoItemDAO
import models.TodoItem
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}

trait ITodoItemService {
  def getItem(uuid: UUID): Future[Option[TodoItem]]
  def getAllItems: Future[Seq[TodoItem]]
  def createItem(todolistItem: TodoItem): Future[WriteResult]
  def updateItem(todolistItem: TodoItem): Future[Option[TodoItem]]
  def deleteItem(uuid: UUID): Future[Option[TodoItem]]
  def deleteAllItems(): Future[WriteResult]
}

class TodoItemService @Inject() (todoItemDAO: TodoItemDAO)
    extends ITodoItemService {
  def getItem(uuid: UUID): Future[Option[TodoItem]] = todoItemDAO.getById(uuid)
  def getAllItems: Future[Seq[TodoItem]] = todoItemDAO.getAll
  def createItem(todoItem: TodoItem): Future[WriteResult] =
    todoItemDAO.create(todoItem)
  def updateItem(todoItem: TodoItem): Future[Option[TodoItem]] =
    todoItemDAO.update(todoItem)
  def updateAllItems(isCompleted: Boolean): Future[UpdateWriteResult] =
    todoItemDAO.updateAll(isCompleted)
  def deleteItem(uuid: UUID): Future[Option[TodoItem]] =
    todoItemDAO.delete(uuid)
  def deleteAllItems(): Future[WriteResult] = todoItemDAO.deleteAll()

}
