package controllers

import json.reads.CategoryStoreReads.categoryStoreReads
import lib.persistence.onMySQL.CategoryRepository
import play.api.i18n.I18nSupport
import play.api.libs.json.{ JsError, Json }
import play.api.mvc.{ BaseController, ControllerComponents }
import json.writes.CategoryWrites.categoryWrites
import lib.model.Category

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class CategoryApiController @Inject() (val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController with I18nSupport {

  // categoryテーブルのレコード一覧をJson形式で返すメソッド
  def list() = Action async { implicit req =>
    for {
      allCategory <- CategoryRepository.getAll()
    } yield {
      Ok(Json.toJson(allCategory))
    }
  }

  // categoryレコードを追加するメソッド
  def store() = Action(parse.json) async { implicit req =>
    req.body.validate[Category.WithNoId].fold(
      error => Future.successful(BadRequest(JsError.toJson(error))),
      category => {
        for {
          result <- CategoryRepository.add(category)
        } yield {
          Ok(Json.toJson(result.toLong))
        }
      }
    )
  }
}
