package model.view.viewvalues

import model.form.formdata.TodoFormData
import play.api.data.Form

// todo/store(Todo追加フォーム)ページのviewvalue
case class ViewValueTodoStore(
  title:   String,
  cssSrc:  Seq[String],
  jsSrc:   Seq[String],
  form:    Form[TodoFormData],
) extends ViewValueCommon