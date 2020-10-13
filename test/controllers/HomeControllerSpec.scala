package controllers

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
import play.api.libs.json.JodaWrites._
import org.bson.types.ObjectId
import org.joda.time.DateTime
import services.TodoItemService
import models.TodoItem

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

  "HomeController#getTodoItem [GET /api/todo-items/:id]" should {
    "return 200 Ok" in {
      val id = "507f1f77bcf86cd799439011"
      val fakeRequest =
        FakeRequest(GET, s"api/todo-items/$id", FakeHeaders(), AnyContent())
      when(todoItemService.getItem(id))
        .thenReturn(Future.successful(Some(TodoItem(new ObjectId(id)))))
      val result = controller.getTodoItem(id)(fakeRequest)
      status(result) mustBe OK
    }
  }

  "HomeController#addTodoItem [POST /api/todo-item]" should {
    "return 201 Created" in {
      val currentDateTime = DateTime.now
      val id = "507f1f77bcf86cd799439011"
      val json = Json.obj(
        "id" -> id,
        "name" -> "do test",
        "is_completed" -> false,
        "created" -> Json.toJson(currentDateTime),
        "updated" -> Json.toJson(currentDateTime)
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
            new ObjectId(id),
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
      val id = "507f1f77bcf86cd799439011"
      val json = Json.obj(
        "id" -> id,
        "name" -> "do test2",
        "is_completed" -> false,
        "created" -> Json.toJson(currentDateTime),
        "updated" -> Json.toJson(currentDateTime)
      )
      val fakeRequest =
        FakeRequest(PUT, "/api/todo-item", fakeJsonHeaders, json)
      when(
        todoItemService.updateItem(
          TodoItem(
            new ObjectId(id),
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
              new ObjectId(id),
              "do test2",
              isCompleted = false,
              currentDateTime,
              currentDateTime
            )
          )
        )
      )
      val result = controller.updateTodoItem()(fakeRequest)
      status(result) mustBe 200
    }
  }

  "HomeController#updateAllTodoItems [PATCH /api/todo-items/is-completed]" should {
    "return 204 NoContent" in {
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

  "HomeController#removeTodoItem [DELETE /api/todo-item/:id]" should {
    "return 200 Ok" in {
      val id = "507f1f77bcf86cd799439011"
      val fakeRequest =
        FakeRequest(DELETE, s"api/todo-item/$id", FakeHeaders(), AnyContent())
      when(todoItemService.deleteItem(id))
        .thenReturn(Future.successful(Some(TodoItem(new ObjectId(id)))))
      val result = controller.removeTodoItem(id)(fakeRequest)
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
