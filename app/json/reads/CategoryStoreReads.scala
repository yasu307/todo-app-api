package json.reads

import play.api.libs.json.{ JsPath, Reads }
import lib.model.{ Category }
import play.api.libs.functional.syntax.toFunctionalBuilderOps

object CategoryStoreReads {
  import EnvReads._
  implicit val categoryStoreReads: Reads[Category.WithNoId] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "slug").read[String] and
      (JsPath \ "color" \ "code").read[Category.Color]
  )(Category(_: String, _: String, _: Category.Color))
}
