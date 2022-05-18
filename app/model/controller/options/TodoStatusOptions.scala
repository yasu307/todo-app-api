package model.controller.options

import lib.model.Todo

object TodoStatusOptions{
  // フォームにて使用するTodo.Statusのラジオボタンフォームの選択肢一覧
  final val todoStatusOpt: Seq[(String, String)] = Todo.Status.values.map{state => (state.code.toString, state.name)}
}
