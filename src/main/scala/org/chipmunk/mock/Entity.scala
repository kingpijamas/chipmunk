package org.chipmunk.mock

import org.chipmunk.persistent.relation.Association2
import org.chipmunk.Declaration
import org.chipmunk.Identifiable
import org.chipmunk.persistent
import org.squeryl.dsl.{ ManyToMany => SManyToMany }
import org.squeryl.dsl.ManyToManyRelation
import org.squeryl.dsl.{ ManyToOne => SManyToOne }
import org.squeryl.dsl.{ OneToMany => SOneToMany }
import org.squeryl.dsl.OneToManyRelation

trait Entity[T <: org.chipmunk.persistent.Entity[T]] extends org.chipmunk.persistent.Entity[T] {
  self: T =>

  private[mock] var isMockPersisted: Boolean = false

  override def isPersisted: Boolean = isMockPersisted

  override protected def owner[O](
    relation: => Declaration[OneToManyRelation[T, O]])
  : SOneToMany[O] =
    OneToMany[O]()

  override protected def owner[R <: Identifiable](
    relation: => Declaration[ManyToManyRelation[T, R, Association2]])
  : SManyToMany[R, Association2] =
    ManyToMany[R](this.id, owningSide = true)

  override protected def ownee[O <: Identifiable](
    relation: => Declaration[OneToManyRelation[O, T]])
  : SManyToOne[O] =
    ManyToOne[O]()

  override protected def ownee[L <: Identifiable](
    relation: => Declaration[ManyToManyRelation[L, T, Association2]])
  : SManyToMany[L, Association2] =
    ManyToMany[L](this.id, owningSide = false)

  override protected def relate[O <: Identifiable](
    relation: SOneToMany[O],
    other: O)
  : Unit = {
    relation.assign(other)
  }

  override protected def relate[O <: Identifiable](
    relation: SManyToMany[O, _],
    other: O)
  : Unit = {
    relation.assign(other)
  }
}
