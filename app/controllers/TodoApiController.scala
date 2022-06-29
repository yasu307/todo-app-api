package controllers

import controllers.util.FutureResultConverter.FutureResultConverter
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
    val dbAction = for {
      allTodo <- TodoRepository.getAll()
    } yield {
      Ok(Json.toJson(allTodo))
    }
    dbAction.recoverServerError
  }

  // to_doレコードを追加するメソッド
  def store() = Action(parse.json) async { implicit req =>
    req.body.validate[Todo.WithNoId].fold(
      error => Future.successful(BadRequest(JsError.toJson(error))),
      todo => {
        val dbAction = for {
          result <- TodoRepository.add(todo)
        } yield {
          Ok(Json.toJson(result.toLong))
        }
        dbAction.recoverServerError
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
          val dbAction = for {
            result <- TodoRepository.update(todo)
          } yield {
            result match {
              // リクエストから受け取ったtodoのidプロパティに対応するtodoが存在しなかった場合
              case None    => NotFound(Json.toJson(s"No todo with id ${todo.id.toLong}"))
              case Some(_) => Ok(Json.toJson(result))
            }
          }
          dbAction.recoverServerError
        }
      }
    )
  }

  // to_doレコードを削除するメソッド
  def delete(todoId: Long) = Action async { implicit req =>
    val dbAction = for {
      result <- TodoRepository.remove(Todo.Id(todoId))
    } yield {
      result match {
        // リクエストから受け取ったtodoIdに対応するtodoが存在しなかった場合
        case None    => NotFound(Json.toJson(s"No todo with id ${todoId.toLong}"))
        case Some(_) => Ok(Json.toJson(result))
      }
    }
    dbAction.recoverServerError
  }
}
