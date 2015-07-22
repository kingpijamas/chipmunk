package org.chipmunk.persistent

import org.chipmunk.Declaration
import org.chipmunk.Identifiable
import org.chipmunk.Keyed
import org.squeryl.dsl.ManyToMany
import org.squeryl.dsl.ManyToManyRelation
import org.squeryl.dsl.ManyToOne
import org.squeryl.dsl.OneToMany
import org.squeryl.dsl.OneToManyRelation

trait Entity[T <: Entity[T]] extends Identifiable with Keyed {
  self: T =>

  protected def owner[O](
    relation: => Declaration[OneToManyRelation[T, O]])
  : OneToMany[O] = {
    relation.value.left(this)
  }

  protected def owner[R <: Identifiable](
    relation: => Declaration[ManyToManyRelation[T, R, Association2]])
  : ManyToMany[R, Association2] = {
    relation.value.left(this)
  }

  protected def ownee[O <: Identifiable](
    relation: => Declaration[OneToManyRelation[O, T]])
  : ManyToOne[O] = {
    relation.value.right(this)
  }

  protected def ownee[L <: Identifiable](
    relation: => Declaration[ManyToManyRelation[L, T, Association2]])
  : ManyToMany[L, Association2] = {
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
