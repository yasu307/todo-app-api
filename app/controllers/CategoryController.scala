package controllers

import lib.model.Category
import lib.persistence.onMySQL.CategoryRepository
import model.controller.options.CategoryColorOptions
import model.form.formdata.CategoryFormData
import model.view.viewvalues.{ViewValueCategoryList, ViewValueCategoryStore, ViewValueHome}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

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

  // to_do_categoryレコードを追加するメソッド
  def store() = Action async { implicit request: Request[AnyContent] =>
    CategoryFormData.form.bindFromRequest().fold(
      // 処理が失敗した場合に呼び出される関数
      (formWithErrors: Form[CategoryFormData]) => {
        val vv = ViewValueCategoryStore(
          title    = "カテゴリ追加画面",
          cssSrc   = Seq("category/category-store.css"),
          jsSrc    = Seq("main.js"),
          form     = formWithErrors,
          colorOpt = CategoryColorOptions.categoryColorOpt,
        )
        Future.successful(BadRequest(views.html.category.Store(vv)))
      },
      // 処理が成功した場合に呼び出される関数
      (categoryFormData: CategoryFormData) => {
        for {
          _ <- CategoryRepository.add(Category(categoryFormData.name, categoryFormData.slug, categoryFormData.color))
        } yield {
          Redirect(routes.CategoryController.list)
        }
      }
    )
  }

  // to_do_categoryレコードの追加内容を入力するformを表示するメソッド
  def register() = Action { implicit req =>
    val vv = ViewValueCategoryStore(
      title    = "カテゴリ追加画面",
      cssSrc   = Seq("category/category-store.css"),
      jsSrc    = Seq("main.js"),
      form     = CategoryFormData.form,
      colorOpt = CategoryColorOptions.categoryColorOpt,
    )
    Ok(views.html.category.Store(vv))
  }
}
