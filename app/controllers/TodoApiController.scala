package controllers

import json.writes.TodoWrites.todoWrites
import lib.persistence.onMySQL.TodoRepository
import play.api.i18n.I18nSupport
import play.api.libs.json.{ Json }
import play.api.mvc.{ BaseController, ControllerComponents }

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext }

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
}
