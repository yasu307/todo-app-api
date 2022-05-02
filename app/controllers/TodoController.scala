package controllers

import lib.model._
import lib.persistence.onMySQL.TodoRepository
import model._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TodoController @Inject()(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController with I18nSupport {
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

  // Todo追加画面で使用するFormオブジェクト
  val form = Form(
    mapping(
      "categoryId" -> longNumber(),
      "title"      -> nonEmptyText(maxLength = 255),
      "body"       -> text()
    )(TodoFormData.apply)(TodoFormData.unapply)
  )

  // to_doレコードを追加するメソッド
  def store() = Action async { implicit request: Request[AnyContent] =>
    form.bindFromRequest().fold(
      // 処理が失敗した場合に呼び出される関数
      (formWithErrors: Form[TodoFormData]) => {
        val vv = ViewValueTodoStore(
          title  = "Todo追加画面",
          cssSrc = Seq("todo/todo-list.css"),
          jsSrc  = Seq("main.js"),
          form   = formWithErrors
        )
        Future.successful(BadRequest(views.html.todo.store(vv)))
      },
      // 処理が成功した場合に呼び出される関数
      (todoFormData: TodoFormData) => {
        for{
          _ <- TodoRepository.add(Todo.apply(todoFormData.categoryId, todoFormData.title, todoFormData.body))
        } yield {
          Redirect(routes.TodoController.list)
        }
      }
    )
  }

  // to_doレコードの追加内容を入力するformを表示するメソッド
  def register() = Action { implicit req =>
    val vv = ViewValueTodoStore(
      title  = "Todo追加画面",
      cssSrc = Seq("todo/todo-list.css"),
      jsSrc  = Seq("main.js"),
      form   = form
    )
    Ok(views.html.todo.store(vv))
  }
}
