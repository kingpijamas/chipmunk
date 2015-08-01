package org.chipmunk.persistent.relation.impl

import org.chipmunk.mock
import org.chipmunk.persistent.Entity
import org.chipmunk.persistent.relation.ManyToMany
import org.chipmunk.persistent.relation.ManyToMany.SManyToMany
import org.chipmunk.persistent.relation.PersistentLike
import org.chipmunk.persistent.relation.TransientLike
import org.chipmunk.persistent.relation.RelationProxy
import scala.annotation.meta.field

object ManyToManyImpl {
  type ManyToManyImpl[O <: Entity[_]] = ManyToMany[O] with RelationProxy[O]

  def apply[E <: Entity[_], O <: Entity[_]](
    owner: E,
    owningSide: Boolean,
    actualRel: SManyToMany[O])
  : ManyToManyImpl[O] = {
    val rel = mock.ManyToMany[O](owner.id, owningSide)
    ManyToManyImpl[O](actualRel, rel)
  }

  def apply[O <: Entity[_]](
    actualRel: SManyToMany[O], rel: SManyToMany[O]): ManyToManyImpl[O] =
    new TransientManyToMany[O](actualRel, rel)
}

private class TransientManyToMany[O <: Entity[_]](
  @(transient @field) val actualRel: SManyToMany[O],
  @(transient @field) val rel: SManyToMany[O])
    extends ManyToMany[O] with TransientLike[O] {

  def persist(): PersistentManyToMany[O] = {
    if (isDirty) {
      rel.associationMap foreach {
        case (other, assoc) =>
          if (!other.isPersisted) {
            other.persistBody()
          }
          actualRel.associate(other, assoc)
      }
    }
    new PersistentManyToMany[O](actualRel)
  }
}

private class PersistentManyToMany[O <: Entity[_]](
    @(transient @field) val rel: SManyToMany[O])
  extends ManyToMany[O] with PersistentLike[O]
