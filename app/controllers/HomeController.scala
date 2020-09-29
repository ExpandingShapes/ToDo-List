package controllers

import javax.inject._
import models.TodolistItem
import play.api._
import play.api.mvc._
import play.modules.reactivemongo.{
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}
import play.mvc.Controller
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}
//
import play.api.Logger
import play.api.mvc.{AbstractController, ControllerComponents}

import play.modules.reactivemongo.{ // ReactiveMongo Play2 plugin
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (
    override val controllerComponents: ControllerComponents,
    val reactiveMongoApi: ReactiveMongoApi
) extends AbstractController(controllerComponents)
    with MongoController
    with ReactiveMongoComponents {

  val logger: Logger = Logger(this.getClass)

  implicit def ec: ExecutionContext = controllerComponents.executionContext

  def collection: Future[JSONCollection] =
    database.map(_.collection[JSONCollection]("persons"))

  def index(): Action[AnyContent] =
    Action.async { implicit request: Request[AnyContent] =>
      {
        logger.info("Entered index method")
        val item = TodolistItem("test2")
        val futureResult: Future[WriteResult] =
          collection.flatMap(_.insert.one(item)) //insert.one(item))
        futureResult.map(_ => Ok(views.html.index(item)))
        //Ok(views.html.index(item))
      }
    }
}
