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
  def update() = Action(parse.json) async { implicit req =>
    req.body.validate[Todo.EmbeddedId].fold(
      error => Future.successful(BadRequest(JsError.toJson(error))),
      todo => {
        for {
          result <- TodoRepository.update(todo)
        } yield {
          Ok(Json.toJson(result))
        }
      }
    )
  }
}
