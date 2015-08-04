package org.chipmunk.relation.persistent

import org.chipmunk.entity.Entity
import org.chipmunk.relation.OneToMany
import org.chipmunk.relation.OneToMany.SOneToMany
import org.chipmunk.relation.PersistentLike
import org.chipmunk.relation.TransientLike
import org.chipmunk.relation.mock
import scala.annotation.meta.field
import org.chipmunk.relation.RelationProxy

object OneToManyImpl {
  type OneToManyImpl[O <: Entity[_]] = OneToMany[O] with RelationProxy[O]

  def apply[O <: Entity[_]](
    actualRel: SOneToMany[O],
    rel: SOneToMany[O] = mock.OneToMany[O]()): OneToManyImpl[O] =
    new TransientOneToMany[O](actualRel, rel)
}

private class TransientOneToMany[O <: Entity[_]](
  @(transient @field) val actualRel: SOneToMany[O],
  @(transient @field) val rel: SOneToMany[O])
    extends OneToMany[O] with TransientLike[O] {

  def persist(): PersistentOneToMany[O] = {
    if (isDirty) {
      rel foreach { other =>
        // TODO: CHECK!!! apparently this is not necessary for the
        // case where the many part is unrelated (it'll probably fail for the rest)
        // if (!other.isPersisted) {
        //    other.persistBody()
        // }
        actualRel.associate(other)
      }
    }
    new PersistentOneToMany[O](actualRel)
  }
}

private class PersistentOneToMany[O <: Entity[_]](
  @(transient @field) val rel: SOneToMany[O])
    extends OneToMany[O] with PersistentLike[O]
