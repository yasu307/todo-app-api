package model.form.formdata

import lib.model.{ Category, Todo }
import play.api.data.Form
import play.api.data.Forms._

// Todo更新画面のフォームでユーザが入力する値を格納するデータ
//
// 実装方法決定理由:
// Todoモデルのインスタンスを元にこのフォームデータを埋めた結果を取得するには
// TodoEditFormData.form.fill(todo) とする。
case class TodoEditFormData(
  categoryId: Category.Id,
  title:      String,
  body:       String,
  state:      Todo.Status,
)

object TodoEditFormData {
  // Todo更新画面で使用するFormオブジェクト
  val form = Form(
    mapping(
      "categoryId" -> longNumber.transform[Category.Id](l => Category.Id(l), _.toLong),
      "title"      -> nonEmptyText(maxLength = 255),
      "body"       -> text(),
      "state"      -> longNumber.transform[Todo.Status](l => Todo.Status(l.toShort), _.code),
    )(TodoEditFormData.apply)(TodoEditFormData.unapply)
  )

  // TodoモデルからTodoEdiFormDataを作成する
  //
  // implicit conversionを使うことでTodoEditFormDataが要求された場所にTodo.EmbeddedIdを渡した場合このメソッドが実行される
  implicit def apply(todo: Todo.EmbeddedId): TodoEditFormData = TodoEditFormData(
    todo.v.categoryId,
    todo.v.title,
    todo.v.body,
    todo.v.state
  )
}
