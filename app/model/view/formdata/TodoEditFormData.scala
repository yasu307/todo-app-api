package model.view.formdata

import lib.model.Todo
import play.api.data.Form
import play.api.data.Forms._

// Todo更新画面のフォームで使用するデータ
case class TodoEditFormData(
  categoryId: Long, //todo TodoCategoryをインポートして型を指定する必要がある？
  title:      String,
  body:       String,
  state:      Short,
)

object TodoEditFormData {
  // Todo更新画面で使用するFormオブジェクト
  val form = Form(
    mapping(
      "categoryId" -> longNumber(),
      "title"      -> nonEmptyText(maxLength = 255),
      "body"       -> text(),
      "state"      -> shortNumber(),
    )(TodoEditFormData.apply)(TodoEditFormData.unapply)
  )

  // Todoモデルと密結合になるためこのメソッドは良くない？
  def fillFromTodo(todo: Todo.EmbeddedId) = {
    form.fill(TodoEditFormData(
      todo.v.categoryId,
      todo.v.title,
      todo.v.body,
      todo.v.state.code))
  }
}