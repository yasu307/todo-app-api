package model.form.formdata

import lib.model.Category
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints

// カテゴリ追加画面のフォームで使用するデータ
case class CategoryFormData(
  name:  String,
  slug:  String,
  color: Category.Color,
)

object CategoryFormData {
  // カテゴリ追加画面で使用するFormオブジェクト
  val form = Form(
    mapping(
      "name"  -> nonEmptyText(maxLength = 255),
      // MySQLに英数字のみ許可する制約があるが、formでもチェックしないとエラー画面に移動してしまうので追加
      "slug"  -> nonEmptyText(maxLength = 64).verifying(Constraints.pattern("""[ -~]+""".r, error = "英数字で入力してください")),
      // formでhelperを使っていないのでこのマッピングは使用されていない？
      "color" -> longNumber.transform[Category.Color](l => Category.Color(l.toShort), _.code),
    )(CategoryFormData.apply)(CategoryFormData.unapply)
  )

  // Category.EmbeddedIdからCategoryFormDataを作成する
  implicit def apply(category: Category.EmbeddedId): CategoryFormData = CategoryFormData(
    category.v.name,
    category.v.slug,
    category.v.color,
  )
}