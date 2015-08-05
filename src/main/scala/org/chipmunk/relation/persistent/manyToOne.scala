package org.chipmunk.relation.persistent

import org.chipmunk.entity.Entity
import org.chipmunk.relation.ManyToOne
import org.chipmunk.relation.ManyToOne.SManyToOne
import org.chipmunk.relation.PersistentLike
import org.chipmunk.relation.TransientLike
import org.chipmunk.relation.mock
import scala.annotation.meta.field
import org.chipmunk.relation.RelationProxy

object ManyToOneImpl {
  type ManyToOneImpl[O <: Entity[_]] = ManyToOne[O] with RelationProxy[O]

  def apply[O <: Entity[_]](
    actualRel: SManyToOne[O],
    rel: SManyToOne[O] = mock.ManyToOne[O]()): ManyToOneImpl[O] =
    new TransientManyToOne[O](actualRel, rel)
}

private class TransientManyToOne[O <: Entity[_]](
  @(transient @field) val actualRel: SManyToOne[O],
  @(transient @field) val rel: SManyToOne[O])
    extends ManyToOne[O] with TransientLike[O] {

  def persist(): PersistentManyToOne[O] = {
    // FIXME: far from ideal, but will work if the O2M on
    // the other side is used correctly
    if (isDirty) {
      actualRel foreach { _.persistBody() }
    }
    new PersistentManyToOne[O](actualRel)
  }
}

private class PersistentManyToOne[O <: Entity[_]](
  @(transient @field) val rel: SManyToOne[O])
    extends ManyToOne[O] with PersistentLike[O]
