package model.view.viewvalues

import lib.model.Category
import model.form.formdata.TodoFormData
import play.api.data.Form

// todo/store(Todo追加フォーム)ページのviewvalue
case class ViewValueTodoStore(
  title:       String,
  cssSrc:      Seq[String],
  jsSrc:       Seq[String],
  form:        Form[TodoFormData],
  allCategory: Seq[Category.EmbeddedId],
) extends ViewValueCommon
