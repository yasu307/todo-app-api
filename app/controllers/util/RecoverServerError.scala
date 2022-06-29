package controllers.util
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.InternalServerError

import scala.concurrent.{ ExecutionContext, Future }

// Future[Result]にメソッドを追加する
object FutureResultConverter {
  implicit def FutureResultConverter(dbAction: Future[Result])(implicit ec: ExecutionContext) = new {
    // サーバーエラーからエラーメッセージを抜き出しJSONに変換するメソッド
    def recoverServerError = {
      dbAction.recover { case e =>
        InternalServerError(Json.obj("error" -> e.getMessage))
      }
    }
  }
}
