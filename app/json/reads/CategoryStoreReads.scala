package json.reads

import play.api.libs.json.{ JsPath, Reads }
import lib.model.{ Category }
import play.api.libs.functional.syntax.toFunctionalBuilderOps

object CategoryStoreReads {
  implicit val categoryStoreReads: Reads[Category.WithNoId] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "slug").read[String] and
      (JsPath \ "color" \ "code").read[Short].map(Category.Color.apply)
  )(Category.createWithNoId _)
}
