package org.chipmunk.entity.relation.proxy

import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.ManyToOne
import org.chipmunk.entity.relation.ManyToOne.SManyToOne
import org.chipmunk.entity.relation.mock
import scala.annotation.meta.field

object ManyToOneProxy {
  type ManyToOneProxy[O <: Entity[_]] = ManyToOne[O] with RelationProxy[O]

  private[entity] def apply[O <: Entity[_]](
    actualRel: SManyToOne[O],
    rel: SManyToOne[O] = mock.ManyToOne[O]())
  : ManyToOneProxy[O] =
    new TransientM2OProxy[O](actualRel, rel)
}

private class TransientM2OProxy[O <: Entity[_]](
  @(transient @field) val actualRel: SManyToOne[O],
  @(transient @field) val rel: SManyToOne[O])
    extends ManyToOne[O] with TransientLike[O] {

  def persist(): PersistentM2OProxy[O] = {
    // FIXME: far from ideal, but will work if the O2M on
    // the other side is used correctly
    if (isDirty) {
      //TODO: this line below assumes a non-standard behavior only mock.ManyToOne has!
      rel foreach { _.persistBody() }
    }
    new PersistentM2OProxy[O](actualRel)
  }
}

private class PersistentM2OProxy[O <: Entity[_]](
  @(transient @field) val rel: SManyToOne[O])
    extends ManyToOne[O] with PersistentLike[O]
