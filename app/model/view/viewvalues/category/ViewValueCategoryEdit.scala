package model.view.viewvalues

import lib.model.Category
import model.form.formdata.CategoryFormData
import play.api.data.Form

// category/edit(カテゴリ更新フォーム)ページのviewvalue
case class ViewValueCategoryEdit(
  title:       String,
  cssSrc:      Seq[String],
  jsSrc:       Seq[String],
  form:        Form[CategoryFormData],
  colorValues: List[Category.Color],
  categoryId:  Long,
) extends ViewValueCommon