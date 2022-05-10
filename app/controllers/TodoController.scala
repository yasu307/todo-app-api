package controllers

import lib.model._
import lib.persistence.onMySQL.TodoRepository
import model.view.viewvalues.{ViewValueHome, ViewValueTodoList, ViewValueTodoStore, ViewValueTodoEdit}
import model.view.formdata.{TodoEditFormData, TodoFormData}
import model.controller.options.TodoStatusOptions

import play.api.Logger
import play.api.data.Form
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

    val todoWithNoId = Todo(555L, "テキスト", "本文本文本文本文本文本文本文本文")
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
      Ok(views.html.todo.List(vv))
    }
  }

  // to_doレコードを追加するメソッド
  def store() = Action async { implicit request: Request[AnyContent] =>
    TodoFormData.form.bindFromRequest().fold(
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
          _ <- TodoRepository.add(Todo(todoFormData.categoryId, todoFormData.title, todoFormData.body))
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
      form   = TodoFormData.form
    )
    Ok(views.html.todo.store(vv))
  }

  // to_doの内容を編集する画面を表示するメソッド
  // todoをそのままeditの引数にしなかった理由 -> routes参照
  def edit(todoId: Long) = Action async { implicit req =>
    for{
      todoOpt <- TodoRepository.get(Todo.Id(todoId))
    }yield{
      todoOpt match {
        // todoIdに対応するtodoレコードがあればそのtodoを更新する画面に遷移する
        case Some(todo) =>
          val vv = ViewValueTodoEdit(
            title     = "Todo更新画面",
            cssSrc    = Seq("main.css"),
            jsSrc     = Seq("main.js"),
            form      = TodoEditFormData.fillFromTodo(todo),
            statusOpt = TodoStatusOptions.todoStatusOpt,
            todoId    = todoId
          )
          Ok(views.html.todo.Edit(vv))
        // todoIdに対応するtodoレコードが取得できなければTodo一覧表示画面に遷移する
        case _ =>
          // NotFound画面に置き換える
          Redirect(routes.TodoController.list())
      }
    }
  }

  // 既存のto_doレコードを更新するメソッド
  def update(todoId: Long) = Action async { implicit req =>
    TodoEditFormData.form.bindFromRequest().fold(
      formWithErrors => {
        val vv = ViewValueTodoEdit(
          title     = "Todo更新画面",
          cssSrc    = Seq("main.css"),
          jsSrc     = Seq("main.js"),
          form      = formWithErrors,
          statusOpt = TodoStatusOptions.todoStatusOpt,
          todoId    = todoId
        )
        Future.successful(BadRequest(views.html.todo.Edit(vv)))
      },
      todoEditFormData => {
        for{
          count <- TodoRepository.updateById(Todo.Id(todoId), todoEditFormData.categoryId, todoEditFormData.title, todoEditFormData.body, todoEditFormData.state)
        } yield {
          count match{
            case None => Redirect(routes.TodoController.list)
            case _ => Redirect(routes.TodoController.list)
          }
        }
      }
    )
  }
}
