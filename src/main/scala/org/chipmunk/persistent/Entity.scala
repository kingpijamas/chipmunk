package org.chipmunk.persistent

import org.squeryl.dsl.ManyToMany
import org.squeryl.dsl.ManyToManyRelation
import org.squeryl.dsl.ManyToOne
import org.squeryl.dsl.OneToMany
import org.squeryl.dsl.OneToManyRelation
import org.chipmunk.DeclaredRelation
import org.chipmunk.BinaryAssociation
import org.chipmunk.Identifiable
import org.chipmunk.Keyed

trait Entity[T <: Entity[T]] extends Identifiable with Keyed {
  self: T =>

  protected def owner[O](
    relation: => DeclaredRelation[OneToManyRelation[T, O]])
  : OneToMany[O] = {
    relation.value.left(this)
  }

  protected def owner[R <: Identifiable](
    relation: => DeclaredRelation[ManyToManyRelation[T, R, BinaryAssociation]])
  : ManyToMany[R, BinaryAssociation] = {
    relation.value.left(this)
  }

  protected def ownee[O <: Identifiable](
    relation: => DeclaredRelation[OneToManyRelation[O, T]])
  : ManyToOne[O] = {
    relation.value.right(this)
  }

  protected def ownee[L <: Identifiable](
    relation: => DeclaredRelation[ManyToManyRelation[L, T, BinaryAssociation]])
  : ManyToMany[L, BinaryAssociation] = {
    relation.value.right(this)
  }

  protected def relate[O <: Identifiable](
    relation: OneToMany[O],
    other: O)
  : Unit = {
    failIfNotPersisted()

    relation.associate(other)
  }

  protected def relate[O <: Identifiable](
    relation: ManyToMany[O, _],
    other: O)
  : Unit = {
    failIfNotPersisted()

    relation.associate(other)
  }

  protected def failIfNotPersisted(): Unit = {
    if (!isPersisted) {
      throw new IllegalStateException(
        f"Entity must be persisted before relating it! ($this%s)")
    }
  }
}
