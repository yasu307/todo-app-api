package controllers

import lib.model.Todo
import lib.persistence.onMySQL.TodoRepository
import model.ViewValueHome
import play.api.Logger
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class TodoController @Inject()(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController {
  val logger: Logger = Logger(this.getClass())

  def debug() = Action async{ implicit req =>
    val vv = ViewValueHome(
      title  = "Home",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js")
    )

    val todoWithNoId = Todo.apply(555L, "テキスト", "本文本文本文本文本文本文本文本文")
    for {
      todoId <- TodoRepository.add(todoWithNoId)
      todoFromDB <- TodoRepository.get(Todo.Id(todoId))
      updatedTodo <- TodoRepository.update(todoFromDB.get.map(_.copy(title="updated")))
      deletedTodo <- TodoRepository.remove(updatedTodo.get.id)
    } yield {
      logger.debug("add: " + todoId.toString)
      logger.debug("get: " + todoFromDB.toString)
      logger.debug("update: " + updatedTodo.toString)
      logger.debug("delete: " + deletedTodo.toString)
      Ok(views.html.Home(vv))
    }
  }
}
