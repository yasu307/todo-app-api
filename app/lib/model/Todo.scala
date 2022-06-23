package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// Todoを表すモデル
//~~~~~~~~~~~~~~~~~~~~
import Todo._
case class Todo(
  id:         Option[Id],
  categoryId: Category.Id,
  title:      String,
  body:       String,
  state:      Status,
  updatedAt:  LocalDateTime = NOW,
  createdAt:  LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object Todo {
  val Id = the[Identity[Id]]
  type Id         = Long @@ Todo
  type WithNoId   = Entity.WithNoId[Id, Todo]
  type EmbeddedId = Entity.EmbeddedId[Id, Todo]

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class Status(val code: Short, val name: String) extends EnumStatus
  object Status extends EnumStatus.Of[Status] {
    case object TODO      extends Status(code = 0, name = "TODO")
    case object WORKING   extends Status(code = 1, name = "進行中")
    case object COMPLETED extends Status(code = 2, name = "完了")
  }

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(categoryId: Category.Id, title: String, body: String): WithNoId = {
    new Entity.WithNoId(
      new Todo(
        id         = None,
        categoryId = categoryId,
        title      = title,
        body       = body,
        state      = Status.TODO
      )
    )
  }

  // 上記applyメソッドの別名を作成
  // TodoStoreReadsにて関数オブジェクトに変換して用いる
  // applyは同名の関数が存在しているので関数オブジェクトに指定できない。そのため、このような処理をしている
  def createWithNoId(categoryId: Category.Id, title: String, body: String) = apply(categoryId, title, body)

  // EmbeddedIdを作成するメソッド
  def apply(todoId: Todo.Id, categoryId: Category.Id, title: String, body: String, state: Todo.Status): EmbeddedId = {
    new Todo(
      id         = Some(todoId),
      categoryId = categoryId,
      title      = title,
      body       = body,
      state      = state,
    ).toEmbeddedId
  }

  // 上記applyメソッドの別名を作成
  // TodoUpdateReadsにて関数オブジェクトに変換して用いる
  // applyは同名の関数が存在しているので関数オブジェクトに指定できない。そのため、このような処理をしている
  def createEmbeddedId(todoId: Todo.Id, categoryId: Category.Id, title: String, body: String, state: Todo.Status) = apply(todoId, categoryId, title, body, state)
}
