/**
 *
 * from to do sample project
 *
 */

package controllers

import model.view.viewvalues.ViewValueHome
import javax.inject._
import play.api.mvc._
import play.api.Logger

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  val logger: Logger = Logger(this.getClass())

  // Home画面を表示するメソッド
  def index() = Action { implicit req =>
    val vv = ViewValueHome(
      title  = "Home",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js")
    )
    Ok(views.html.Home(vv))
  }
}
