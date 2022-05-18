package model.view.viewvalues

// NotFoundページのviewvalue
case class ViewValueNotFound(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
) extends ViewValueCommon