package lib.persistence

import ixias.persistence.SlickRepository
import lib.model.Todo
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

// TodoRepository: TodoTableへのクエリ発行を行うRepository層の定義
//~~~~~~~~~~~~~~~~~~~~~~
case class TodoRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[Todo.Id, Todo, P]
    with db.SlickResourceProvider[P] {

  import api._

  /**
    * Get Todo Data
    */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(TodoTable, "slave") {
      _
        .filter(_.id === id)
        .result.headOption
    }

  /**
    * Get all Todo Data
    */
  def getAll(): Future[Seq[EntityEmbeddedId]] =
    RunDBAction(TodoTable, "slave") {
      _.result
    }

  /**
    * Add Todo Data
    */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(TodoTable) { slick =>
      (slick returning slick.map(_.id)) += entity.v
    }

  /**
    * Update Todo Data columns except id and created_at
    */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(TodoTable) { slick =>
      val row = slick.filter(_.id === entity.id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row
              .map(p => (p.categoryId, p.title, p.body ?, p.state))
              .update(entity.v.categoryId, entity.v.title, entity.v.body, entity.v.state)
        }
      } yield old
    }

  /**
    * Delete Todo Data
    */
  def remove(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(TodoTable) { slick =>
      val row = slick.filter(_.id === id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }
}
