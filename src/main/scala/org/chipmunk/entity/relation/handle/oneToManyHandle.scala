package org.chipmunk.entity.relation.handle

import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.ManyToOne
import org.chipmunk.entity.relation.OneToMany
import org.chipmunk.entity.relation.OneToMany.{ SOneToMany => SO2M }
import org.chipmunk.entity.relation.mock
import scala.annotation.meta.field

object OneToManyHandle {
  def apply[O <: Entity[O], M <: Entity[M]](
    owner: O,
    sqrlRelOf: O => SO2M[M],
    unsetFk: M => Unit,
    transientRel: mock.OneToMany[M] = new mock.OneToMany[M]())
  : OneToManyHandle[M] = {
    val state = if (!owner.isPersisted)
      new TransientO2MState(owner, transientRel, sqrlRelOf, unsetFk)
    else
      new PersistentO2MState(owner, sqrlRelOf(owner), unsetFk)

    new OneToManyHandle(state)
  }
}

class OneToManyHandle[M <: Entity[_]] private[handle] (
  @(transient @field) private[handle] var state: OneToManyState[M])
    extends RelationHandle[M] with OneToMany[M] {

  def persist(): Unit = { state = state.persist() }

  override def -=(other: M): this.type = {
    state -= other
    this
  }

  def toSqueryl: SO2M[M] = state.rel
}

sealed trait OneToManyState[M <: Entity[_]] extends RelationStateLike[M] {
  final type SRel = OneToMany.SOneToMany[M]

  private[handle] def -=(other: M): Unit

  override def persist(): OneToManyState[M]
}

private class TransientO2MState[O <: Entity[O], M <: Entity[M]](
  val owner: O,
  val rel: mock.OneToMany[M],
  val sqrlRel: O => SO2M[M],
  val unsetFk: M => Unit)
    extends OneToManyState[M] with TransientStateLike[M] {

  private[handle] def -=(other: M): Unit = { rel -= other }

  def persist(): PersistentO2MState[O, M] = {
    val squerylO2M = sqrlRel(owner)
    if (isDirty) {
      rel foreach { other =>
        if (!other.isPersisted) { other.persistBody() }
        squerylO2M.associate(other)
      }
    }
    new PersistentO2MState[O, M](owner, squerylO2M, unsetFk)
  }
}

private class PersistentO2MState[O <: Entity[O], M <: Entity[M]](
  val owner: O,
  val rel: SO2M[M],
  val unsetFk: M => Unit)
    extends OneToManyState[M] with PersistentStateLike[M] {

  private[handle] def -=(other: M): Unit = {
    val table = other.table
    unsetFk(other)
    table.update(other)
  }
}
