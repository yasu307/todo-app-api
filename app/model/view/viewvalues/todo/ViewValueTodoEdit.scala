package model.view.viewvalues

import model.view.formdata.TodoEditFormData
import play.api.data.Form

// todo/edit(Todo更新フォーム)ページのviewvalue
case class ViewValueTodoEdit(
  title:   String,
  cssSrc:  Seq[String],
  jsSrc:   Seq[String],
  form:    Form[TodoEditFormData],
  todoId:  Long,
) extends ViewValueCommon