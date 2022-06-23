package json.reads

import play.api.libs.json.{ JsPath, Reads }
import lib.model.{ Category, Todo }
import play.api.libs.functional.syntax.toFunctionalBuilderOps

object TodoUpdateReads {
  implicit val todoUpdateReads: Reads[Todo.EmbeddedId] = (
    (JsPath \ "id").read[Long].map(Todo.Id.apply) and
      (JsPath \ "categoryId").read[Long].map(Category.Id.apply) and
      (JsPath \ "title").read[String] and
      (JsPath \ "body").read[String] and
      (JsPath \ "state" \ "code").read[Short].map(Todo.Status.apply)
  )(Todo.createEmbeddedId _)
}
