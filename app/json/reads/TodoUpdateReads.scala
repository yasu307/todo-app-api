package json.reads

import play.api.libs.json.{ JsPath, Reads }
import lib.model.{ Category, Todo }
import play.api.libs.functional.syntax.toFunctionalBuilderOps

object TodoUpdateReads {
  import EnvReads._
  implicit val todoUpdateReads: Reads[Todo.EmbeddedId] = (
    (JsPath \ "id").read[Todo.Id] and
      (JsPath \ "categoryId").read[Category.Id] and
      (JsPath \ "title").read[String] and
      (JsPath \ "body").read[String] and
      (JsPath \ "state" \ "code").read[Todo.Status]
  )(Todo.createEmbeddedId _)
}
