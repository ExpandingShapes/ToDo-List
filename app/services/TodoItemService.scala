package services

import java.util.UUID
import daos.TodoItemDAO
import javax.inject.Inject
import models.TodoItem
import scala.concurrent.Future
import reactivemongo.api.commands.WriteResult

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
  def deleteItem(uuid: UUID): Future[Option[TodoItem]] =
    todoItemDAO.delete(uuid)
  def deleteAllItems(): Future[WriteResult] = todoItemDAO.deleteAll()

}
