package controllers

import lib.persistence.onMySQL.{ CategoryRepository }
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{ BaseController, ControllerComponents }
import json.writes.CategoryWrites.categoryWrites

import javax.inject.Inject
import scala.concurrent.ExecutionContext

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
}
