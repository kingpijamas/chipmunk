package org.chipmunk.relation.persistent

import org.chipmunk.relation.mock
import org.chipmunk.relation.ManyToMany
import org.chipmunk.relation.ManyToMany.SManyToMany
import org.chipmunk.relation.PersistentLike
import org.chipmunk.relation.TransientLike
import org.chipmunk.relation.RelationProxy
import scala.annotation.meta.field
import org.chipmunk.entity.Entity

object ManyToManyImpl {
  type ManyToManyImpl[O <: Entity[_]] = ManyToMany[O] with RelationProxy[O]

  def apply[O <: Entity[_]](
    owner: Entity[_],
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
