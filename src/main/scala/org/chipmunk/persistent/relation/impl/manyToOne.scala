package org.chipmunk.persistent.relation.impl

import org.chipmunk.mock
import org.chipmunk.persistent.Entity
import org.chipmunk.persistent.relation.ManyToOne
import org.chipmunk.persistent.relation.PersistentLike
import org.chipmunk.persistent.relation.RelationProxy
import org.chipmunk.persistent.relation.TransientLike
import org.chipmunk.persistent.relation.ManyToOne.SManyToOne
import scala.annotation.meta.field

object ManyToOneImpl {
  type ManyToOneImpl[O <: Entity[_]] = ManyToOne[O] with RelationProxy[O]

  def apply[O <: Entity[_]](
    actualRel: SManyToOne[O],
    rel: SManyToOne[O] = mock.ManyToOne[O]())
  : ManyToOneImpl[O] =
    new TransientManyToOne[O](actualRel, rel)
}

private class TransientManyToOne[O <: Entity[_]](
  @(transient @field) val actualRel: SManyToOne[O],
  @(transient @field) val rel: SManyToOne[O])
    extends ManyToOne[O] with TransientLike[O] {

  // do nothing, as per squeryl's ManyToOne limitations,
  // the one responsible for persisting ManyToOnes is the OneToMany
  def persist(): PersistentManyToOne[O] = new PersistentManyToOne[O](actualRel)
}

private class PersistentManyToOne[O <: Entity[_]](
  @(transient @field) val rel: SManyToOne[O])
    extends ManyToOne[O] with PersistentLike[O]
