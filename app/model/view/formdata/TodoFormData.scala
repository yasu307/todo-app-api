package model.view.formdata

import play.api.data.Form
import play.api.data.Forms._

// Todo追加画面のフォームで使用するデータ
case class TodoFormData(
  categoryId: Long, //todo TodoCategoryをインポートして型を指定する必要がある？
  title:      String,
  body:       String
)

object TodoFormData {
  // Todo追加画面で使用するFormオブジェクト
  val form = Form(
    mapping(
      "categoryId" -> longNumber(),
      "title"      -> nonEmptyText(maxLength = 255),
      "body"       -> text()
    )(TodoFormData.apply)(TodoFormData.unapply)
  )
}