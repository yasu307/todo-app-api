package model.view.viewvalues

import lib.model.Category
import model.form.formdata.CategoryFormData
import play.api.data.Form

// category/store(カテゴリ追加フォーム)ページのviewvalue
case class ViewValueCategoryStore(
  title:       String,
  cssSrc:      Seq[String],
  jsSrc:       Seq[String],
  form:        Form[CategoryFormData],
  colorValues: List[Category.Color],
) extends ViewValueCommon
