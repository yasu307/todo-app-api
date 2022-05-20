package controllers

import lib.model.Category
import lib.persistence.onMySQL.CategoryRepository
import model.view.viewvalues.{ViewValueHome, ViewValueCategoryList}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CategoryController @Inject()(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController with I18nSupport {
  val logger: Logger = Logger(this.getClass())

  // to_do_categoryテーブルの操作をデバックするためのメソッド　
  // テーブル操作の結果はlogに出力する
  def debug() = Action async { implicit req =>
    val vv = ViewValueHome(
      title  = "Home",
      cssSrc = Seq("home.css"),
      jsSrc  = Seq("main.js")
    )

    val categoryWithNoId = Category("デザイナー", "design", Category.Color.RED)
    for {
      categoryId      <- CategoryRepository.add(categoryWithNoId)
      categoryFromId  <- CategoryRepository.get(Category.Id(categoryId))
      updatedCategory <- CategoryRepository.update(categoryFromId.get.map(_.copy(name = "updated")))
      deletedCategory <- CategoryRepository.remove(updatedCategory.get.id)
    } yield {
      logger.debug("add: " + categoryId.toString)
      logger.debug("get: " + categoryFromId.toString)
      logger.debug("update: " + updatedCategory.toString)
      logger.debug("delete: " + deletedCategory.toString)
      Ok(views.html.Home(vv))
    }
  }

  // to_do_categoryテーブルのレコード一覧を表示するメソッド
  def list() = Action async { implicit req =>
    for {
      allCategory <- CategoryRepository.getAll()
    } yield {
      val vv = ViewValueCategoryList(
        title       = "カテゴリ一覧",
        cssSrc      = Seq("category/category-list.css"),
        jsSrc       = Seq("main.js"),
        allCategory = allCategory,
      )
      Ok(views.html.category.List(vv))
    }
  }
}
