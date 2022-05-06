package model.view.ViewValues

import lib.model.Todo

// todo/list(Todo一覧)ページのviewvalue
case class ViewValueTodoList(
  title:   String,
  cssSrc:  Seq[String],
  jsSrc:   Seq[String],
  allTodo: Seq[Todo.EmbeddedId],
) extends ViewValueCommon