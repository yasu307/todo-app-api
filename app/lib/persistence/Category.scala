package lib.persistence

import ixias.persistence.SlickRepository
import lib.model.Category
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

// CategoryRepository: CategoryTableへのクエリ発行を行うRepository層の定義
//~~~~~~~~~~~~~~~~~~~~~~
case class CategoryRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[Category.Id, Category, P]
    with db.SlickResourceProvider[P] {

  import api._

  /**
    * Get Category Data
    */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(CategoryTable, "slave") {
      _.filter(_.id === id).result.headOption
    }

  /**
    * Get all Category Data
    */
  def getAll(): Future[Seq[EntityEmbeddedId]] =
    RunDBAction(CategoryTable, "slave") {
      _.result
    }

  /**
    * Add Category Data
    */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(CategoryTable) { slick =>
      (slick returning slick.map(_.id)) += entity.v
    }

  /**
    * Update Category Data columns except id and created_at
    */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(CategoryTable) { slick =>
      val row = slick.filter(_.id === entity.id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row
              .map(p => (p.name, p.slug, p.color))
              .update(entity.v.name, entity.v.slug, entity.v.color)
        }
      } yield old
    }

  /**
    * Delete Category Data
    */
  def remove(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(CategoryTable) { slick =>
      val row = slick.filter(_.id === id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }

  /**
    * Delete a category data and update todo datas related to the category
    */
  def removeCategoryAndUpdateRelatedTodos(id: Id): Future[Option[EntityEmbeddedId]] =
    // 2つのテーブルを使うため、DBActionを入れ子にする
    DBAction(CategoryTable) { case (db, categorySlick) =>
      DBAction(TodoTable) { case (_, todoSlick) =>
        val deleteCategoryRow = categorySlick.filter(_.id === id)
        val updateTodosQuery  = todoSlick.filter(_.categoryId === id).map(_.categoryId).update(Category.deletedId)
        db.run(
          for {
            old <- deleteCategoryRow.result.headOption
            -   <- old match {
              case None    => DBIO.successful(0)
              case Some(_) => (deleteCategoryRow.delete andFinally updateTodosQuery).transactionally
            }
          } yield old
        )
      }
    }
}
