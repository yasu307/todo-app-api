package controllers

import lib.model.Todo
import lib.persistence.onMySQL.TodoRepository
import model.{ViewValueHome, ViewValueTodoList}
import play.api.Logger
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class TodoController @Inject()(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController {
  val logger: Logger = Logger(this.getClass())

  // to_doテーブルの操作をデバックするためのメソッド　
  // テーブル操作の結果はlogに出力する
  def debug() = Action async{ implicit req =>
    val vv = ViewValueHome(
      title  = "Home",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js")
    )

    val todoWithNoId = Todo.apply(555L, "テキスト", "本文本文本文本文本文本文本文本文")
    for {
      todoId      <- TodoRepository.add(todoWithNoId)
      todoFromDB  <- TodoRepository.get(Todo.Id(todoId))
      updatedTodo <- TodoRepository.update(todoFromDB.get.map(_.copy(title="updated")))
      deletedTodo <- TodoRepository.remove(updatedTodo.get.id)
    } yield {
      logger.debug("add: "    + todoId.toString)
      logger.debug("get: "    + todoFromDB.toString)
      logger.debug("update: " + updatedTodo.toString)
      logger.debug("delete: " + deletedTodo.toString)
      Ok(views.html.Home(vv))
    }
  }

  // to_doテーブルのレコード一覧を表示するメソッド
  def list() = Action async{ implicit req =>
    for{
      allTodo <- TodoRepository.getAll()
    }yield{
      val vv = ViewValueTodoList(
        title   = "Todo 一覧",
        cssSrc  = Seq("todo/todo-list.css"),
        jsSrc   = Seq("main.js"),
        allTodo = allTodo
      )
      Ok(views.html.todo.list(vv))
    }
  }
}
