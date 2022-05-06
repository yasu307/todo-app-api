/**
 *
 * from to do sample project
 *
 */

package model.view.ViewValues

// Topページのviewvalue
case class ViewValueHome(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
) extends ViewValueCommon

