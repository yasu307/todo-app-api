package model.controller.options

import lib.model.Category

object CategoryColorOptions{
  // フォームにて使用するCategory.Colorのラジオボタンフォームの選択肢一覧
  final val categoryColorOpt: Seq[(String, String)] = Category.Color.values.map{state => (state.code.toString, state.toString)}
}
