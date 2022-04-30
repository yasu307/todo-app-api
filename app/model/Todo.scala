package model

import lib.persistence.onMySQL.TodoRepository.EntityEmbeddedId

// TodoListページのviewvalue
case class ViewValueTodoList(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
  allTodo: Seq[EntityEmbeddedId],
) extends ViewValueCommon