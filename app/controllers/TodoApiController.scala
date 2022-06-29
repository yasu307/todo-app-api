package controllers

import json.writes.TodoWrites.todoWrites
import json.reads.TodoStoreReads.todoStoreReads
import json.reads.TodoUpdateReads.todoUpdateReads
import lib.model.Todo
import lib.persistence.onMySQL.TodoRepository
import play.api.i18n.I18nSupport
import play.api.libs.json.{ JsError, Json }
import play.api.mvc.{ BaseController, ControllerComponents }

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class TodoApiController @Inject() (val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController with I18nSupport {

  // to_doテーブルのレコード一覧をJson形式で返すメソッド
  def list() = Action async { implicit req =>
    for {
      allTodo <- TodoRepository.getAll()
    } yield {
      Ok(Json.toJson(allTodo))
    }
  }

  // to_doレコードを追加するメソッド
  def store() = Action(parse.json) async { implicit req =>
    req.body.validate[Todo.WithNoId].fold(
      error => Future.successful(BadRequest(JsError.toJson(error))),
      todo => {
        for {
          result <- TodoRepository.add(todo)
        } yield {
          Ok(Json.toJson(result.toLong))
        }
      }
    )
  }

  // to_doレコードを更新するメソッド
  def update(todoId: Long) = Action(parse.json) async { implicit req =>
    req.body.validate[Todo.EmbeddedId].fold(
      error => Future.successful(BadRequest(JsError.toJson(error))),
      todo => {
        // URLから取得したTodo.Idとリクエストボディに含まれているTodoデータのIdが異なっていたらBadRequestを返す
        if (todoId != todo.id.toLong) {
          Future.successful(BadRequest(Json.toJson("URL or Request body is wrong")))
        } else {
          for {
            result <- TodoRepository.update(todo)
          } yield {
            Ok(Json.toJson(result))
          }
        }
      }
    )
  }

  // to_doレコードを削除するメソッド
  def delete(todoId: Long) = Action async { implicit req =>
    for {
      result <- TodoRepository.remove(Todo.Id(todoId))
    } yield {
      Ok(Json.toJson(result))
    }
  }
}
