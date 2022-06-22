package json.writes

import lib.model.{ Todo }
import play.api.libs.json.{ JsNumber, Json, Writes }

object TodoWrites {

  // Todo.StatusからJSONへの変換
  // todoWritesにて用いられる
  implicit val stateWrites = new Writes[Todo.Status] {
    def writes(state: Todo.Status) = Json.obj(
      "code" -> state.code,
      "name" -> state.name
    )
  }

  // Todo.EmbeddedIdからJSONへの変換
  implicit val todoWrites = new Writes[Todo.EmbeddedId] {
    def writes(todo: Todo.EmbeddedId) = Json.obj(
      "id"         -> JsNumber(todo.id),
      "categoryId" -> JsNumber(todo.v.categoryId),
      "title"      -> todo.v.title,
      "body"       -> todo.v.body,
      "state"      -> todo.v.state,
      "updatedAt"  -> todo.v.updatedAt,
      "createdAt"  -> todo.v.createdAt
    )
  }
}
