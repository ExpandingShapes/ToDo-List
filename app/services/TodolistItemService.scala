package services

import java.util.UUID

import daos.TodolistItemDAO
import models.TodolistItem

import scala.concurrent.Future

trait ITodolistItemService {
  def getItem(uuid: UUID): Option[TodolistItem]
  def getAllItems: Future[List[TodolistItem]]
  def saveItem(todolistItem: TodolistItem): Int
  def updateItem(todolistItem: TodolistItem): Int
  def deleteItem(uuid: UUID): Int
}

class TodolistItemService extends ITodolistItemService {
  val todolistItemDAO: TodolistItemDAO = new TodolistItemDAO

  def getItem(uuid: UUID): Option[TodolistItem] = ???
  def getAllItems: Future[List[TodolistItem]] = todolistItemDAO.getAll
  def saveItem(todolistItem: TodolistItem): Int = ???
  def updateItem(todolistItem: TodolistItem): Int = ???
  def deleteItem(uuid: UUID): Int = ???
}
