/**
  * from to do sample project
  */

package model.view.viewvalues

// Topページのviewvalue
case class ViewValueHome(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
) extends ViewValueCommon
