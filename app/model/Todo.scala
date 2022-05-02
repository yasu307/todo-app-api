package model

import lib.persistence.onMySQL.TodoRepository.EntityEmbeddedId

// todoo/list(Todo一覧)ページのviewvalue
case class ViewValueTodoList(
  title:   String,
  cssSrc:  Seq[String],
  jsSrc:   Seq[String],
  allTodo: Seq[EntityEmbeddedId],
) extends ViewValueCommon