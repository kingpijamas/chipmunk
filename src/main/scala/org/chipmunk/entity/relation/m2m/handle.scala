package org.chipmunk.entity.relation.m2m

import scala.annotation.meta.field
import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.PersistentStateLike
import org.chipmunk.entity.relation.RelationStateLike
import org.chipmunk.entity.relation.TransientStateLike
import org.chipmunk.entity.relation.m2m.ManyToMany.SManyToMany
import org.chipmunk.test.{ relation => mock }
import org.chipmunk.entity.relation.RelationHandle

object ManyToManyHandle {
  def apply[O <: Entity[_]](
    owner: Entity[_],
    owningSide: Boolean,
    actualRel: SManyToMany[O])
  : ManyToManyHandle[O] = {
    val transientRel = mock.ManyToMany[O](owner.id, owningSide)
    ManyToManyHandle(!owner.isPersisted, owningSide, actualRel, transientRel)
  }

  private def apply[O <: Entity[_]](
    transient: Boolean,
    owningSide: Boolean,
    actualRel: SManyToMany[O],
    transientRel: SManyToMany[O])
  : ManyToManyHandle[O] = {
    val state = if (transient)
      new TransientM2MState(owningSide, actualRel, transientRel)
    else
      new PersistentM2MState(owningSide, actualRel)

    new ManyToManyHandle(owningSide, state)
  }
}

class ManyToManyHandle[O <: Entity[_]] private[m2m] (
  @(transient @field) protected val isOwningSide: Boolean,
  @(transient @field) private[m2m] var state: ManyToManyState[O])
    extends RelationHandle[O] with ManyToMany[O] {

  def persist(): Unit = { state = state.persist() }

  def toSqueryl: SManyToMany[O] = state.rel
}

sealed trait ManyToManyState[O <: Entity[_]] extends RelationStateLike[O] {
  final type SRel = ManyToMany.SManyToMany[O]

  override def persist(): ManyToManyState[O]
}

private class TransientM2MState[O <: Entity[_]](
  protected val isOwningSide: Boolean,
  val actualRel: SManyToMany[O],
  val rel: SManyToMany[O])
    extends ManyToManyState[O] with TransientStateLike[O] {

  def persist(): PersistentM2MState[O] = {
    if (isDirty) {
      rel.associationMap foreach { case (other, assoc) =>
        if (!other.isPersisted) { other.persistBody() }
        actualRel.associate(other, assoc)
      }
    }
    new PersistentM2MState[O](isOwningSide, actualRel)
  }
}

private class PersistentM2MState[O <: Entity[_]](
  protected val isOwningSide: Boolean,
  val rel: SManyToMany[O])
    extends ManyToManyState[O] with PersistentStateLike[O]
