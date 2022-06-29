package model.form.formdata

import lib.model.Category
import play.api.data.Form
import play.api.data.Forms._

// Todo追加画面のフォームで使用するデータ
case class TodoFormData(
  categoryId: Category.Id,
  title:      String,
  body:       Option[String]
)

object TodoFormData {
  // Todo追加画面で使用するFormオブジェクト
  val form = Form(
    mapping(
      "categoryId" -> longNumber.transform[Category.Id](l => Category.Id(l), _.toLong),
      "title"      -> nonEmptyText(maxLength = 255),
      "body"       -> optional(text)
    )(TodoFormData.apply)(TodoFormData.unapply)
  )
}
