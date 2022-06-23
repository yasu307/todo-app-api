package json.reads

import play.api.libs.json.{ JsPath, Reads }
import lib.model.{ Category }
import play.api.libs.functional.syntax.toFunctionalBuilderOps

object CategoryUpdateReads {
  implicit val categoryUpdateReads: Reads[Category.EmbeddedId] = (
    (JsPath \ "id").read[Long].map(Category.Id.apply) and
      (JsPath \ "name").read[String] and
      (JsPath \ "slug").read[String] and
      (JsPath \ "color" \ "code").read[Short].map(Category.Color.apply)
  )(Category.createEmbeddedId _)
}
