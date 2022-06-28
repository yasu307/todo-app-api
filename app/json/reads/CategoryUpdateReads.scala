package json.reads

import play.api.libs.json.{ JsPath, Reads }
import lib.model.{ Category }
import play.api.libs.functional.syntax.toFunctionalBuilderOps

object CategoryUpdateReads {
  import EnvReads._
  implicit val categoryUpdateReads: Reads[Category.EmbeddedId] = (
    (JsPath \ "id").read[Category.Id] and
      (JsPath \ "name").read[String] and
      (JsPath \ "slug").read[String] and
      (JsPath \ "color" \ "code").read[Category.Color]
  )(Category.createEmbeddedId _)
}
