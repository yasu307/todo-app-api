package model.view.viewvalues

import lib.model.{Category, Todo}

// todo/list(Todo一覧)ページのviewvalue
case class ViewValueTodoList(
  title:       String,
  cssSrc:      Seq[String],
  jsSrc:       Seq[String],
  allTodo:     Seq[Todo.EmbeddedId],
  allCategory: Seq[Category.EmbeddedId],
) extends ViewValueCommon