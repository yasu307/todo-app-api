package model.controller.formatter

import lib.model.Todo
import play.api.data.FormError
import play.api.data.format.Formatter

object implicits{
  // Todo.Status型をFormの引数にできるように
  implicit object StatusFormatter extends Formatter[Todo.Status]{
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Todo.Status] = {
      data.get(key).map(i => Todo.Status(i.toShort)).toRight(Seq(FormError(key, "error.required", Nil)))
    }
    override def unbind(key: String, value: Todo.Status): Map[String, String] = {
      Map(key -> value.code.toString)
    }
  }
}
