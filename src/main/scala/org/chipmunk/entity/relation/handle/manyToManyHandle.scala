package org.chipmunk.entity.relation.handle

import org.chipmunk.entity.relation.mock
import org.chipmunk.entity.relation.ManyToMany
import org.chipmunk.entity.relation.ManyToMany.SManyToMany
import scala.annotation.meta.field
import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.ManyToOne

object ManyToManyHandle {
  def apply[O <: Entity[_]](
    transient: Boolean,
    owner: Entity[_],
    owningSide: Boolean,
    actualRel: SManyToMany[O])
  : ManyToManyHandle[O] = {
    val transientRel = mock.ManyToMany[O](owner.id, owningSide)
    ManyToManyHandle(transient, actualRel, transientRel)
  }

  def apply[O <: Entity[_]](
    transient: Boolean,
    actualRel: SManyToMany[O],
    transientRel: SManyToMany[O])
  : ManyToManyHandle[O] = {
    val state = if (transient)
      new TransientM2MState(actualRel, transientRel)
    else
      new PersistentM2MState(actualRel)

    new ManyToManyHandle(state)
  }
}

class ManyToManyHandle[O <: Entity[_]] private[handle] (
  private[this] var state: ManyToManyState[O])
    extends RelationHandle[O] with ManyToMany[O] {

  def persist(): Unit = { state = state.persist() }

  def toSqueryl: SManyToMany[O] = state.rel
}

sealed trait ManyToManyState[O <: Entity[_]] extends RelationStateLike[O] {
  final type SRel = ManyToMany.SManyToMany[O]

  override def persist(): ManyToManyState[O]
}

private class TransientM2MState[O <: Entity[_]](
  val actualRel: SManyToMany[O],
  val rel: SManyToMany[O])
    extends ManyToManyState[O] with TransientStateLike[O] {

  def persist(): PersistentM2MState[O] = {
    if (isDirty) {
      rel.associationMap foreach {
        case (other, assoc) =>
          if (!other.isPersisted) {
            other.persistBody()
          }
          actualRel.associate(other, assoc)
      }
    }
    new PersistentM2MState[O](actualRel)
  }
}

private class PersistentM2MState[O <: Entity[_]](
  val rel: SManyToMany[O])
    extends ManyToManyState[O] with PersistentStateLike[O]
