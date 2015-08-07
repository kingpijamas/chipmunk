package org.chipmunk.entity.relation.proxy

import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.OneToMany
import org.chipmunk.entity.relation.OneToMany.SOneToMany
import org.chipmunk.entity.relation.mock
import scala.annotation.meta.field

object OneToManyProxy {
  type OneToManyProxy[O <: Entity[_]] = OneToMany[O] with RelationProxy[O]

  private[entity] def apply[O <: Entity[_]](
    actualRel: SOneToMany[O],
    rel: SOneToMany[O] = mock.OneToMany[O]())
  : OneToManyProxy[O] =
    new TransientO2MProxy[O](actualRel, rel)
}

private class TransientO2MProxy[O <: Entity[_]](
  @(transient @field) val actualRel: SOneToMany[O],
  @(transient @field) val rel: SOneToMany[O])
    extends OneToMany[O] with TransientLike[O] {

  def persist(): PersistentO2MProxy[O] = {
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
    new PersistentO2MProxy[O](actualRel)
  }
}

private class PersistentO2MProxy[O <: Entity[_]](
  @(transient @field) val rel: SOneToMany[O])
    extends OneToMany[O] with PersistentLike[O]
