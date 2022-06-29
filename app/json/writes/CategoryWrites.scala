package json.writes

import lib.model.Category
import play.api.libs.json.{ JsNumber, Json, Writes }

object CategoryWrites {

  // Category.ColorからJSONへの変換
  // categoryWritesにて用いられる
  implicit val colorWrites = new Writes[Category.Color] {
    def writes(color: Category.Color) = Json.obj(
      "code" -> color.code,
      "rgb"  -> Json.obj(
        "red"   -> color.rgb.r,
        "green" -> color.rgb.g,
        "blue"  -> color.rgb.b
      )
    )
  }

  // Category.EmbeddedIdからJSONへの変換
  implicit val categoryWrites = new Writes[Category.EmbeddedId] {
    def writes(category: Category.EmbeddedId) = Json.obj(
      "id"        -> JsNumber(category.id),
      "name"      -> category.v.name,
      "slug"      -> category.v.slug,
      "color"     -> category.v.color,
      "updatedAt" -> category.v.updatedAt,
      "createdAt" -> category.v.createdAt
    )
  }
}
