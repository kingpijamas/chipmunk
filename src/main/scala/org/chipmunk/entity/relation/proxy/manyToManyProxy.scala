package org.chipmunk.entity.relation.proxy

import org.chipmunk.entity.relation.mock
import org.chipmunk.entity.relation.ManyToMany
import org.chipmunk.entity.relation.ManyToMany.SManyToMany
import scala.annotation.meta.field
import org.chipmunk.entity.Entity

object ManyToManyProxy {
  type ManyToManyProxy[O <: Entity[_]] = ManyToMany[O] with RelationProxy[O]

  private[entity] def apply[O <: Entity[_]](
    owner: Entity[_],
    owningSide: Boolean,
    actualRel: SManyToMany[O])
  : ManyToManyProxy[O] = {
    val rel = mock.ManyToMany[O](owner.id, owningSide)
    ManyToManyProxy[O](actualRel, rel)
  }

  private[entity] def apply[O <: Entity[_]](
    actualRel: SManyToMany[O],
    rel: SManyToMany[O])
  : ManyToManyProxy[O] =
    new TransientM2MProxy[O](actualRel, rel)

}

private class TransientM2MProxy[O <: Entity[_]](
  @(transient @field) val actualRel: SManyToMany[O],
  @(transient @field) val rel: SManyToMany[O])
    extends ManyToMany[O] with TransientLike[O] {

  def persist(): PersistentM2MProxy[O] = {
    if (isDirty) {
      rel.associationMap foreach {
        case (other, assoc) =>
          if (!other.isPersisted) {
            other.persistBody()
          }
          actualRel.associate(other, assoc)
      }
    }
    new PersistentM2MProxy[O](actualRel)
  }
}

private class PersistentM2MProxy[O <: Entity[_]](
  @(transient @field) val rel: SManyToMany[O])
    extends ManyToMany[O] with PersistentLike[O]
