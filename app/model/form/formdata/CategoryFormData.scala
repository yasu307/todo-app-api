package model.form.formdata

import lib.model.Category
import play.api.data.Form
import play.api.data.Forms._

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
      "slug"  -> nonEmptyText(maxLength = 64),
      "color" -> longNumber.transform[Category.Color](l => Category.Color(l.toShort), _.code)
    )(CategoryFormData.apply)(CategoryFormData.unapply)
  )
}