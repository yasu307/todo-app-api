package json.reads

import ixias.util.json.JsonEnvReads
import lib.model.{ Category, Todo }
import play.api.libs.json.Reads

object EnvReads extends JsonEnvReads {
  implicit val todoIdReads:        Reads[Todo.Id]        = idAsNumberReads
  implicit val todoStateReads:     Reads[Todo.Status]    = enumReads(Todo.Status)
  implicit val categoryIdReads:    Reads[Category.Id]    = idAsNumberReads
  implicit val categoryColorReads: Reads[Category.Color] = enumReads(Category.Color)
}
