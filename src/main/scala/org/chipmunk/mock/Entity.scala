package org.chipmunk.mock

import org.chipmunk.DeclaredRelation
import org.squeryl.dsl.{ ManyToMany => SManyToMany }
import org.squeryl.dsl.ManyToManyRelation
import org.squeryl.dsl.{ ManyToOne => SManyToOne }
import org.squeryl.dsl.{ OneToMany => SOneToMany }
import org.squeryl.dsl.OneToManyRelation
import org.squeryl.dsl.{ ManyToMany => SManyToMany }
import org.squeryl.dsl.{ ManyToOne => SManyToOne }
import org.squeryl.dsl.{ OneToMany => SOneToMany }
import org.chipmunk.BinaryAssociation
import org.chipmunk.persistent
import org.chipmunk.Identifiable

trait Entity[T <: Entity[T]] extends persistent.Entity[T] {
  self: T =>
  private[mock] var _isMockPersisted: Boolean = false

  override def isPersisted: Boolean = _isMockPersisted

  override protected def owner[O](
    relation: => DeclaredRelation[OneToManyRelation[T, O]]): SOneToMany[O] =
    new OneToMany[O]

  override protected def owner[R <: Identifiable](
    relation: => DeclaredRelation[ManyToManyRelation[T, R, BinaryAssociation]]): SManyToMany[R, BinaryAssociation] =
    new ManyToMany[R](true, _ => this.id)

  override protected def ownee[O <: Identifiable](
    relation: => DeclaredRelation[OneToManyRelation[O, T]]): SManyToOne[O] =
    new ManyToOne[O]

  override protected def ownee[L <: Identifiable](
    relation: => DeclaredRelation[ManyToManyRelation[L, T, BinaryAssociation]]): SManyToMany[L, BinaryAssociation] =
    new ManyToMany[L](false, _ => this.id)

  override protected def relate[O <: Identifiable](relation: SOneToMany[O], other: O): Unit = {
    failIfNotPersisted()

    relation.assign(other)
  }

  override protected def relate[O <: Identifiable](relation: SManyToMany[O, _], other: O): Unit = {
    failIfNotPersisted()

    relation.assign(other)
  }
}