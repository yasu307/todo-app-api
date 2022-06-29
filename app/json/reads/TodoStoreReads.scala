package json.reads

import play.api.libs.json.{ JsPath, Reads }
import lib.model.{ Category, Todo }
import play.api.libs.functional.syntax.toFunctionalBuilderOps

object TodoStoreReads {
  import EnvReads._
  implicit val todoStoreReads: Reads[Todo.WithNoId] = (
    (JsPath \ "categoryId").read[Category.Id] and
      (JsPath \ "title").read[String] and
      (JsPath \ "body").readNullable[String]
  )(Todo(_: Category.Id, _: String, _: Option[String]))
}
