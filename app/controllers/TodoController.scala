package controllers

import lib.model.{Category, Todo}
import lib.persistence.onMySQL.{CategoryRepository, TodoRepository}
import model.view.viewvalues.{ViewValueError, ViewValueHome, ViewValueTodoEdit, ViewValueTodoList, ViewValueTodoStore}
import model.form.formdata.{TodoEditFormData, TodoFormData}
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
      cssSrc = Seq("home.css"),
      jsSrc  = Seq("main.js")
    )

    val todoWithNoId = Todo(Category.Id(555L), "テキスト", "本文本文本文本文本文本文本文本文")
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
    for {
      (allTodo, allCategory) <- TodoRepository.getAll() zip CategoryRepository.getAll()
    } yield {
      val vv = ViewValueTodoList(
        title       = "Todo 一覧",
        cssSrc      = Seq("todo/todo-list.css"),
        jsSrc       = Seq("todo/todo-list.js"),
        allTodo     = allTodo,
        allCategory = allCategory,
      )
      Ok(views.html.todo.List(vv))
    }
  }

  // to_doレコードを追加するメソッド
  def store() = Action async { implicit request: Request[AnyContent] =>
    TodoFormData.form.bindFromRequest().fold(
      // 処理が失敗した場合に呼び出される関数
      (formWithErrors: Form[TodoFormData]) => {
        for {
          allCategory <- CategoryRepository.getAll()
        } yield {
          val vv = ViewValueTodoStore(
            title       = "Todo追加画面",
            cssSrc      = Seq("todo/todo-store.css"),
            jsSrc       = Seq("main.js"),
            form        = formWithErrors,
            allCategory = allCategory,
          )
          BadRequest(views.html.todo.Store(vv))
        }
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
  def register() = Action async  { implicit req =>
    for{
      allCategory <- CategoryRepository.getAll()
    } yield {
      val vv = ViewValueTodoStore(
        title       = "Todo追加画面",
        cssSrc      = Seq("todo/todo-store.css"),
        jsSrc       = Seq("main.js"),
        form        = TodoFormData.form,
        allCategory = allCategory,
      )
      Ok(views.html.todo.Store(vv))
    }
  }

  // to_doの内容を編集する画面を表示するメソッド
  // todoをそのままeditの引数にしなかった理由 -> routes参照
  def edit(todoId: Long) = Action async { implicit req =>
    for {
      (todoOpt, allCategory) <- TodoRepository.get(Todo.Id(todoId)) zip CategoryRepository.getAll()
    }yield{
      todoOpt match {
        // todoIdに対応するtodoレコードがあればそのtodoを更新する画面に遷移する
        case Some(todo) =>
          val vv = ViewValueTodoEdit(
            title       = "Todo更新画面",
            cssSrc      = Seq("todo/todo-edit.css"),
            jsSrc       = Seq("main.js"),
            form        = TodoEditFormData.form.fill(todo),
            statusOpt   = TodoStatusOptions.todoStatusOpt,
            todoId      = todoId,
            allCategory = allCategory,
          )
          Ok(views.html.todo.Edit(vv))
        // todoIdに対応するtodoレコードが取得できなければTodo一覧表示画面に遷移する
        case _ =>
          NotFound(views.html.Error(ViewValueError.error404))
      }
    }
  }

  // 既存のto_doレコードを更新するメソッド
  def update(todoId: Long) = Action async { implicit req =>
    TodoEditFormData.form.bindFromRequest().fold(
      formWithErrors => {
        for {
          allCategory <- CategoryRepository.getAll()
        } yield {
          val vv = ViewValueTodoEdit(
            title     = "Todo更新画面",
            cssSrc    = Seq("todo/todo-edit.css"),
            jsSrc     = Seq("main.js"),
            form      = formWithErrors,
            statusOpt = TodoStatusOptions.todoStatusOpt,
            todoId    = todoId,
            allCategory = allCategory,
          )
          BadRequest(views.html.todo.Edit(vv))
        }
      },
      todoEditFormData => {
        for{
          count <- TodoRepository.update(Todo(Todo.Id(todoId), todoEditFormData.categoryId, todoEditFormData.title, todoEditFormData.body, todoEditFormData.state))
        } yield {
          count match{
            case None => NotFound(views.html.Error(ViewValueError.error404))
            case _    => Redirect(routes.TodoController.list)
          }
        }
      }
    )
  }

  // 既存のto_doレコードを削除するメソッド
  def delete(todoId: Long) = Action async { implicit req =>
    for{
      result <- TodoRepository.remove(Todo.Id(todoId))
    } yield {
      result match {
        case None => NotFound(views.html.Error(ViewValueError.error404))
        case _    => Redirect(routes.TodoController.list)
      }
    }
  }
}
