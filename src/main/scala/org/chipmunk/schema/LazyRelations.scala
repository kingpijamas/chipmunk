package org.chipmunk.schema

import scala.collection.mutable
import org.chipmunk.entity.Entity
import org.squeryl.dsl.OneToManyRelation
import org.squeryl.dsl.{ Relation => SquerylRelation }

trait LazyRelations {
  private[this] var declarations = mutable.Buffer[Declaration[_]]()

  private[schema] def declare[R <: SquerylRelation[_, _]](
    rel: => R): Declaration[R] = {
    val declaration = new RegularDeclaration(rel)
    declarations += declaration
    declaration
  }

  private[schema] def declare[O <: Entity[_], M <: Entity[_]]
    (fk: M => ForeignKey[_])
    (rel: => OneToManyRelation[O, M])
  : OneToManyDeclaration[O, M] = {
    val declaration = new OneToManyDeclaration(fk, rel)
    declarations += declaration
    declaration
  }

  protected def init(): Unit = {
    declarations foreach { _.init() }
  }
}
