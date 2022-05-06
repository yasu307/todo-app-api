package model.view.ViewValues

import model.view.FormData.TodoFormData
import play.api.data.Form

// todo/store(Todo追加フォーム)ページのviewvalue
case class ViewValueTodoStore(
  title:   String,
  cssSrc:  Seq[String],
  jsSrc:   Seq[String],
  form:    Form[TodoFormData],
) extends ViewValueCommon