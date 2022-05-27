package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// Categoryを表すモデル
//~~~~~~~~~~~~~~~~~~~~
import Category._
case class Category(
  id:        Option[Id],
  name:      String,
  slug:      String,
  color:     Color,
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object Category {
  val Id  = the[Identity[Id]]
  type Id = Long @@ Category
  type WithNoId   = Entity.WithNoId[Id, Category]
  type EmbeddedId = Entity.EmbeddedId[Id, Category]

//  ステータス定義
//  ~~~~~~~~~~~~~~~~~
  sealed abstract class Color(val code: Short, val rgb: RGB) extends EnumStatus
  object Color extends EnumStatus.Of[Color] {
    case object RED     extends Color(code = 0, rgb = RGB(255, 0, 0))
    case object GREEN   extends Color(code = 1, rgb = RGB(0, 255, 0))
    case object BLUE    extends Color(code = 2, rgb = RGB(0, 0, 255))
    case object YELLOW  extends Color(code = 3, rgb = RGB(255, 255, 0))
    case object AQUA    extends Color(code = 4, rgb = RGB(0, 255, 255))
    case object FUCHSIA extends Color(code = 5, rgb = RGB(255, 0, 255))
  }

  case class RGB(r: Int = 0, g: Int = 0, b: Int = 0)

  // 前は存在していたが現在は削除されたカテゴリのidを表す変数
  // todoが"どのカテゴリにも紐づいていない"ことを表すために用いる
  // to_doテーブルの設定によりnullや0以下の値は設定できないため、一番使用される可能性が低いLong.MaxValueを用いる
  final val deletedId = Category.Id(Long.MaxValue)

  // WithNoIdを作成するメソッド
  def apply(name: String, slug: String, color: Category.Color): WithNoId = {
    new Entity.WithNoId(
      new Category(
        id    = None,
        name  = name,
        slug  = slug,
        color = color,
      )
    )
  }

  // EmbeddedIdを作成するメソッド
  def apply(categoryId: Category.Id, name: String, slug: String, color: Category.Color): EmbeddedId = {
    new Category(
      id    = Some(categoryId),
      name  = name,
      slug  = slug,
      color = color,
    ).toEmbeddedId
  }
}