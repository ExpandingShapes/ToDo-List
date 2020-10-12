package controllers

import java.util.UUID
import scala.concurrent.Future
import akka.stream.Materializer
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, ControllerComponents, Results}
import play.api.test.Helpers._
import play.api.test._
import reactivemongo.api.commands.{
  DefaultWriteResult,
  UpdateWriteResult,
  Upserted,
  WriteError
}
import models.TodoItem
import org.joda.time.DateTime
import services.TodoItemService

class HomeControllerSpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with Results
    with MockitoSugar {
  private implicit lazy val materializer: Materializer = app.materializer
  private val controllerComponents = mock[ControllerComponents]
  private val todoItemService = mock[TodoItemService]
  private val defaultWriteResultF = Future.successful(
    DefaultWriteResult(
      ok = true,
      n = 0,
      writeErrors = Seq.empty[WriteError],
      writeConcernError = None,
      code = Some(0),
      errmsg = None
    )
  )
  private val updateWriteResultF = Future.successful(
    UpdateWriteResult(
      ok = true,
      n = 0,
      nModified = 2,
      upserted = Seq.empty[Upserted],
      Seq.empty[WriteError],
      writeConcernError = None,
      code = Some(0),
      errmsg = None
    )
  )
  private val fakeJsonHeaders = FakeHeaders(
    Seq("Content-type" -> "application/json")
  )

  private val controller = {
    new HomeController(
      controllerComponents,
      todoItemService
    ) {
      override val controllerComponents: ControllerComponents =
        Helpers.stubControllerComponents()
    }
  }

  "HomeController#getAllTodoItems [GET /api/todo-items]" should {
    "return 200 Ok" in {
      val fakeRequest =
        FakeRequest(GET, "api/todo-items", FakeHeaders(), AnyContent())
      when(todoItemService.getAllItems)
        .thenReturn(Future.successful(Seq.empty[TodoItem]))
      val result =
        controller.getAllTodoItems(fakeRequest)
      status(result) mustBe OK
    }
  }

  "HomeController#getTodoItem [GET /api/todo-items/:uuid]" should {
    "return 200 Ok" in {
      val uuid = UUID.fromString("347f494a-47eb-4d40-be2d-8fe626094932")
      val fakeRequest =
        FakeRequest(GET, s"api/todo-items/$uuid", FakeHeaders(), AnyContent())
      when(todoItemService.getItem(uuid))
        .thenReturn(Future.successful(Some(TodoItem())))
      val result = controller.getTodoItem(uuid)(fakeRequest)
      status(result) mustBe OK
    }
  }

  "HomeController#addTodoItem [POST /api/todo-item]" should {
    "return 201 Created" in {
      val currentDateTime = DateTime.now
      val uuid = UUID.fromString("6a89c002-da0b-4f0d-8829-71062bc7469f")
      val json = Json.obj(
        "uuid" -> uuid.toString,
        "name" -> "do test",
        "is_completed" -> false,
        "created" -> currentDateTime.toString,
        "updated" -> currentDateTime.toString
      )
      val fakeRequest = FakeRequest(
        POST,
        "api/todo-item",
        fakeJsonHeaders,
        json
      )
      when(
        todoItemService.createItem(
          TodoItem(
            uuid,
            "do test",
            isCompleted = false,
            currentDateTime,
            currentDateTime
          )
        )
      ).thenReturn(
        defaultWriteResultF
      )
      val result = controller.addTodoItem()(fakeRequest)
      status(result) mustBe 201
    }
  }

  "HomeController#updateTodoItem [PUT /api/todo-item]" should {
    "return 200 Ok" in {
      val currentDateTime = DateTime.now
      val uuid = UUID.fromString("6a89c002-da0b-4f0d-8829-71062bc7469f")
      val json = Json.obj(
        "uuid" -> uuid,
        "name" -> "do test2",
        "is_completed" -> false,
        "created" -> currentDateTime.toString,
        "updated" -> currentDateTime.toString
      )
      val fakeRequest =
        FakeRequest(PUT, "/api/todo-item", fakeJsonHeaders, json)
      when(
        todoItemService.updateItem(
          TodoItem(
            uuid,
            "do test2",
            isCompleted = false,
            currentDateTime,
            currentDateTime
          )
        )
      ).thenReturn(
        Future.successful(
          Some(
            TodoItem(
              uuid,
              "do test2",
              isCompleted = false,
              currentDateTime,
              currentDateTime
            )
          )
        )
      )
      val result = controller.updateTodoItem()(fakeRequest)
      status(result) mustBe Ok
    }
  }

  "HomeController#updateAllTodoItems [PATCH /api/todo-items/is-completed]" should {
    "return 204 NoContent" in {
      val uuid = UUID.fromString("6a89c002-da0b-4f0d-8829-71062bc7469f")
      val json = Json.obj(
        "is_completed" -> true
      )
      val fakeRequest =
        FakeRequest(
          PATCH,
          "/api/todo-items/is-completed",
          fakeJsonHeaders,
          json
        )
      when(
        todoItemService.updateAllItems(true)
      ).thenReturn(
        updateWriteResultF
      )
      val result = controller.updateAllTodoItems()(fakeRequest)
      status(result) mustBe 204
    }
  }

  "HomeController#removeTodoItem [DELETE /api/todo-item/:uuid]" should {
    "return 200 Ok" in {
      val uuid = UUID.fromString("347f494a-47eb-4d40-be2d-8fe626094932")
      val fakeRequest =
        FakeRequest(DELETE, s"api/todo-item/$uuid", FakeHeaders(), AnyContent())
      when(todoItemService.deleteItem(uuid))
        .thenReturn(Future.successful(Some(TodoItem())))
      val result = controller.removeTodoItem(uuid)(fakeRequest)
      status(result) mustBe OK
    }
  }

  "HomeController#removeAllTodoItems [DELETE /api/todo-items]" should {
    "return 200 Ok" in {
      val fakeRequest =
        FakeRequest(GET, "api/todo-items", FakeHeaders(), AnyContent())
      when(todoItemService.deleteAllItems())
        .thenReturn(
          defaultWriteResultF
        )
      val result = controller.removeAllTodoItems()(fakeRequest)
      status(result) mustBe 204
    }
  }
}
