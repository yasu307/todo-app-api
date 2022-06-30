package controllers

import lib.model.Category
import lib.persistence.onMySQL.CategoryRepository
import model.form.formdata.CategoryFormData
import model.view.viewvalues.{ ViewValueCategoryEdit, ViewValueCategoryList, ViewValueCategoryStore, ViewValueError, ViewValueHome }
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{ AnyContent, BaseController, ControllerComponents, Request }

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class CategoryController @Inject() (val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
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
        jsSrc       = Seq("category/category-list.js"),
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
          title       = "カテゴリ追加画面",
          cssSrc      = Seq("category/category-store.css"),
          jsSrc       = Seq("main.js"),
          form        = formWithErrors,
          colorValues = Category.Color.values,
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
      title       = "カテゴリ追加画面",
      cssSrc      = Seq("category/category-store.css"),
      jsSrc       = Seq("main.js"),
      form        = CategoryFormData.form,
      colorValues = Category.Color.values,
    )
    Ok(views.html.category.Store(vv))
  }

  // to_do_categoryの編集画面を表示するメソッド
  def edit(categoryId: Long) = Action async { implicit req =>
    for {
      categoryOpt <- CategoryRepository.get(Category.Id(categoryId))
    } yield {
      categoryOpt match {
        // categoryIdに対応するcategoryレコードがあればそのcategoryを更新する画面に遷移する
        case Some(category) =>
          val vv = ViewValueCategoryEdit(
            title       = "カテゴリ更新画面",
            cssSrc      = Seq("category/category-edit.css"),
            jsSrc       = Seq("main.js"),
            form        = CategoryFormData.form.fill(category),
            colorValues = Category.Color.values,
            categoryId  = categoryId
          )
          Ok(views.html.category.Edit(vv))
        // categoryIdに対応するcategoryレコードが取得できなければTodo一覧表示画面に遷移する
        case _              =>
          NotFound(views.html.Error(ViewValueError.error404))
      }
    }
  }

  // 既存のto_do_categoryレコードを更新するメソッド
  def update(categoryId: Long) = Action async { implicit req =>
    CategoryFormData.form.bindFromRequest().fold(
      formWithErrors => {
        val vv = ViewValueCategoryEdit(
          title       = "カテゴリ更新画面",
          cssSrc      = Seq("category/category-edit.css"),
          jsSrc       = Seq("main.js"),
          form        = formWithErrors,
          colorValues = Category.Color.values,
          categoryId  = categoryId
        )
        Future.successful(BadRequest(views.html.category.Edit(vv)))
      },
      categoryFormData => {
        for {
          count <- CategoryRepository.update(Category(Category.Id(categoryId), categoryFormData.name, categoryFormData.slug, categoryFormData.color))
        } yield {
          count match {
            case None => NotFound(views.html.Error(ViewValueError.error404))
            case _    => Redirect(routes.CategoryController.list)
          }
        }
      }
    )
  }

  // 既存のto_do_categoryレコードを削除するメソッド
  // 削除するto_do_categoryに紐づけられているto_doレコードも更新する
  def delete(categoryId: Long) = Action async { implicit req =>
    val dbAction = for {
      result <- CategoryRepository.removeCategoryAndUpdateRelatedTodos(Category.Id(categoryId))
    } yield {
      result match {
        // categoryIdに該当するcategoryレコードが存在しなかった場合
        case None => NotFound(views.html.Error(ViewValueError.error404))
        // DB処理が成功した場合
        case _    => Redirect(routes.CategoryController.list)
      }
    }
    // recover内: DBアクセス処理でエラーが発生した場合
    dbAction.recover { case e =>
      logger.error("database error", e)
      val vv = ViewValueError(
        title        = "サーバーエラー",
        statusCode   = 500,
        errorMessage = e.getMessage,
        cssSrc       = Seq("home.css"),
        jsSrc        = Seq("main.js"),
      )
      InternalServerError(views.html.Error(vv))
    }
  }
}
