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
