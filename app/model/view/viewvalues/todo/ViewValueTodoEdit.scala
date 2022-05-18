package model.view.viewvalues

import model.form.formdata.TodoEditFormData
import play.api.data.Form

// todo/edit(Todo更新フォーム)ページのviewvalue
case class ViewValueTodoEdit(
  title:     String,
  cssSrc:    Seq[String],
  jsSrc:     Seq[String],
  form:      Form[TodoEditFormData],
  statusOpt: Seq[(String, String)],
  todoId:    Long,
) extends ViewValueCommon