package org.chipmunk.entity.relation.handle

import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.OneToMany
import org.chipmunk.entity.relation.OneToMany.SOneToMany
import org.chipmunk.entity.relation.mock
import scala.annotation.meta.field

object OneToManyHandle {
  def apply[O <: Entity[_]](
    owner: Entity[_],
    actualRel: SOneToMany[O],
    transientRel: SOneToMany[O] = mock.OneToMany[O]())
  : OneToManyHandle[O] = {
    val state = if (!owner.isPersisted)
      new TransientO2MState(actualRel, transientRel)
    else
      new PersistentO2MState(actualRel)

    new OneToManyHandle(state)
  }
}

class OneToManyHandle[O <: Entity[_]] private[handle] (
  @(transient @field) private[handle] var state: OneToManyState[O])
    extends RelationHandle[O] with OneToMany[O] {

  def persist(): Unit = { state = state.persist() }

  def toSqueryl: SOneToMany[O] = state.rel
}

sealed trait OneToManyState[O <: Entity[_]] extends RelationStateLike[O] {
  final type SRel = OneToMany.SOneToMany[O]

  override def persist(): OneToManyState[O]
}

private class TransientO2MState[O <: Entity[_]](
  val actualRel: SOneToMany[O],
  val rel: SOneToMany[O])
    extends OneToManyState[O] with TransientStateLike[O] {

  def persist(): PersistentO2MState[O] = {
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
    new PersistentO2MState[O](actualRel)
  }
}

private class PersistentO2MState[O <: Entity[_]](
  val rel: SOneToMany[O])
    extends OneToManyState[O] with PersistentStateLike[O]
