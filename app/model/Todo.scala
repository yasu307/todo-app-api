package model

import lib.model.Todo
import lib.model.TodoFormData
import play.api.data.Form

// todo/list(Todo一覧)ページのviewvalue
case class ViewValueTodoList(
  title:   String,
  cssSrc:  Seq[String],
  jsSrc:   Seq[String],
  allTodo: Seq[Todo.EmbeddedId],
) extends ViewValueCommon

// todo/store(Todo追加フォーム)ページのviewvalue
case class ViewValueTodoStore(
  title:   String,
  cssSrc:  Seq[String],
  jsSrc:   Seq[String],
  form:    Form[TodoFormData],
) extends ViewValueCommon