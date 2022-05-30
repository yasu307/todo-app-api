package model.view.viewvalues

// Errorページのviewvalue
case class ViewValueError(
  title:        String,
  statusCode:   Int,
  errorMessage: String,
  cssSrc:       Seq[String],
  jsSrc:        Seq[String],
) extends ViewValueCommon

object ViewValueError {
  // error404ページのviewvalue
  val error404 = ViewValueError(
    title        = "Not Found",
    statusCode   = 404,
    errorMessage = "ページが見つかりません",
    cssSrc       = Seq("home.css"),
    jsSrc        = Seq("main.js"),
  )
}
