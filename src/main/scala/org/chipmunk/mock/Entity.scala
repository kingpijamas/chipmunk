package org.chipmunk.mock

import org.chipmunk.BinaryAssociation
import org.chipmunk.DeclaredRelation
import org.chipmunk.Identifiable
import org.chipmunk.persistent
import org.squeryl.dsl.{ ManyToMany => SManyToMany }
import org.squeryl.dsl.ManyToManyRelation
import org.squeryl.dsl.{ ManyToOne => SManyToOne }
import org.squeryl.dsl.{ OneToMany => SOneToMany }
import org.squeryl.dsl.OneToManyRelation

trait Entity[T <: persistent.Entity[T]] extends persistent.Entity[T] {
  self: T =>

  private[mock] var isMockPersisted: Boolean = false

  override def isPersisted: Boolean = isMockPersisted

  override protected def owner[O](
    relation: => DeclaredRelation[OneToManyRelation[T, O]])
  : SOneToMany[O] =
    OneToMany[O]()

  override protected def owner[R <: Identifiable](
    relation: => DeclaredRelation[ManyToManyRelation[T, R, BinaryAssociation]])
  : SManyToMany[R, BinaryAssociation] =
    ManyToMany[R](this.id, owningSide = true)

  override protected def ownee[O <: Identifiable](
    relation: => DeclaredRelation[OneToManyRelation[O, T]])
  : SManyToOne[O] =
    ManyToOne[O]()

  override protected def ownee[L <: Identifiable](
    relation: => DeclaredRelation[ManyToManyRelation[L, T, BinaryAssociation]])
  : SManyToMany[L, BinaryAssociation] =
    ManyToMany[L](this.id, owningSide = false)

  override protected def relate[O <: Identifiable](
    relation: SOneToMany[O],
    other: O)
  : Unit = {
    failIfNotPersisted()

    relation.assign(other)
  }

  override protected def relate[O <: Identifiable](
    relation: SManyToMany[O, _],
    other: O)
  : Unit = {
    failIfNotPersisted()

    relation.assign(other)
  }
}
