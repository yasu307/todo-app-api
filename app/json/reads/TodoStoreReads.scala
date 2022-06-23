package json.reads

import play.api.libs.json.{ JsPath, Reads }
import lib.model.{ Category, Todo }
import play.api.libs.functional.syntax.toFunctionalBuilderOps

object TodoStoreReads {
  implicit val todoStoreReads: Reads[Todo.WithNoId] = (
    (JsPath \ "categoryId").read[Long].map(Category.Id.apply) and
      (JsPath \ "title").read[String] and
      (JsPath \ "body").read[String]
  )(Todo.createWithNoId _)
}
