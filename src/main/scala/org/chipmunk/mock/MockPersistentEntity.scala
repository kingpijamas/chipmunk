package org.chipmunk.mock

import org.squeryl.KeyedEntity
import org.squeryl.dsl.ManyToMany
import org.squeryl.dsl.ManyToManyRelation
import org.squeryl.dsl.ManyToOne
import org.squeryl.dsl.OneToMany
import org.squeryl.dsl.OneToManyRelation
import org.chipmunk.persistent.PersistentEntity
import org.chipmunk.BinaryAssociation
import org.chipmunk.DeclaredRelation
import org.chipmunk.persistent.Identifiable

trait MockPersistentEntity[T <: PersistentEntity[T]] extends PersistentEntity[T] {
  self: T =>
  private[mock] var _isMockPersisted: Boolean = false

  override def isPersisted: Boolean = _isMockPersisted

  override protected def owner[O](
    relation: => DeclaredRelation[OneToManyRelation[T, O]]): OneToMany[O] =
    new MockOneToMany[O]

  override protected def ownee[O <: Identifiable](
    relation: => DeclaredRelation[OneToManyRelation[O, T]]): ManyToOne[O] =
    new MockManyToOne[O]

  override protected def owner[R <: Identifiable](
    relation: => DeclaredRelation[ManyToManyRelation[T, R, BinaryAssociation]]): ManyToMany[R, BinaryAssociation] =
    new MockManyToMany[R](true, _ => this.id)

  override protected def ownee[L <: Identifiable](
    relation: => DeclaredRelation[ManyToManyRelation[L, T, BinaryAssociation]]): ManyToMany[L, BinaryAssociation] =
    new MockManyToMany[L](false, _ => this.id)

  override protected def relate[O <: Identifiable](relation: OneToMany[O], other: O): Unit = {
    failIfNotPersisted()

    relation.assign(other)
  }

  override protected def relate[O <: Identifiable](relation: ManyToMany[O, _], other: O): Unit = {
    failIfNotPersisted()

    relation.assign(other)
  }
}