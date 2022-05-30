package model.view.viewvalues

import lib.model.Category

// category/list(Category一覧)ページのviewvalue
case class ViewValueCategoryList(
  title:       String,
  cssSrc:      Seq[String],
  jsSrc:       Seq[String],
  allCategory: Seq[Category.EmbeddedId],
) extends ViewValueCommon
