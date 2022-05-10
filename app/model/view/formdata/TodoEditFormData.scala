package model.view.formdata

import lib.model.Todo
import play.api.data.Form
import play.api.data.Forms._

// Todo更新画面のフォームでユーザが入力する値を格納するデータ
case class TodoEditFormData(
  categoryId: Long, //todo TodoCategoryをインポートして型を指定する必要がある？
  title:      String,
  body:       String,
  state:      Todo.Status,
)

object TodoEditFormData {
  import model.controller.formatter.implicits.StatusFormatter
  // Todo更新画面で使用するFormオブジェクト
  val form = Form(
    mapping(
      "categoryId" -> longNumber(),
      "title"      -> nonEmptyText(maxLength = 255),
      "body"       -> text(),
      "state"      -> of[Todo.Status],
    )(TodoEditFormData.apply)(TodoEditFormData.unapply)
  )

  // Todoモデルのインスタンスによりformを埋めるメソッド
  // Todoモデルと密結合になるためこのメソッドは良くない？
  def fillFromTodo(todo: Todo.EmbeddedId) = {
    form.fill(TodoEditFormData(
      todo.v.categoryId,
      todo.v.title,
      todo.v.body,
      todo.v.state))
  }
}