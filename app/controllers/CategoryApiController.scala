package controllers

import controllers.util.FutureResultConverter.FutureResultConverter
import json.reads.CategoryStoreReads.categoryStoreReads
import json.reads.CategoryUpdateReads.categoryUpdateReads
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
    val dbAction = for {
      allCategory <- CategoryRepository.getAll()
    } yield {
      Ok(Json.toJson(allCategory))
    }
    dbAction.recoverServerError
  }

  // categoryレコードを追加するメソッド
  def store() = Action(parse.json) async { implicit req =>
    req.body.validate[Category.WithNoId].fold(
      error => Future.successful(BadRequest(JsError.toJson(error))),
      category => {
        val dbAction = for {
          result <- CategoryRepository.add(category)
        } yield {
          Ok(Json.toJson(result.toLong))
        }
        dbAction.recoverServerError
      }
    )
  }

  // categoryレコードを更新するメソッド
  def update(categoryId: Long) = Action(parse.json) async { implicit req =>
    req.body.validate[Category.EmbeddedId].fold(
      error => Future.successful(BadRequest(JsError.toJson(error))),
      category => {
        // URLから取得したCategory.Idとリクエストボディに含まれているCategoryデータのIdが異なっていたらBadRequestを返す
        if (categoryId != category.id.toLong) {
          Future.successful(BadRequest(Json.toJson("URL or Request body is wrong")))
        } else {
          val dbAction = for {
            result <- CategoryRepository.update(category)
          } yield {
            result match {
              // リクエストから受け取ったcategoryのidプロパティに対応するcategoryが存在しなかった場合
              case None    => NotFound(Json.toJson(s"No category with id ${category.id.toLong}"))
              case Some(_) => Ok(Json.toJson(result))
            }
          }
          dbAction.recoverServerError
        }
      }
    )
  }

  // categoryレコードを削除するメソッド
  def delete(categoryId: Long) = Action async { implicit req =>
    val dbAction = for {
      result <- CategoryRepository.removeCategoryAndUpdateRelatedTodos(Category.Id(categoryId))
    } yield {
      result match {
        // リクエストから受け取ったcategoryIdに対応するcategoryが存在しなかった場合
        case None    => NotFound(Json.toJson(s"No category with id ${categoryId}"))
        case Some(_) => Ok(Json.toJson(result))
      }
    }
    dbAction.recoverServerError
  }
}
