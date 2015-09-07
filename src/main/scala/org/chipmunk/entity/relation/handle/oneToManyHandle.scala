package org.chipmunk.entity.relation.handle

import org.chipmunk.entity.Entity
import org.chipmunk.entity.Identifiable.Id
import org.chipmunk.entity.relation.OneToMany
import org.chipmunk.entity.relation.OneToMany.{ SOneToMany => SO2M }
import org.chipmunk.entity.relation.mock
import org.squeryl.PrimitiveTypeMode.__thisDsl
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import scala.annotation.meta.field
import org.chipmunk.schema.ForeignKey

object OneToManyHandle {
  def apply[O <: Entity[O], M <: Entity[M]](
    owner: O,
    sqrlRelOf: O => SO2M[M],
    fk: M => ForeignKey[_],
    transientRel: mock.OneToMany[M] = new mock.OneToMany[M]())
  : OneToManyHandle[M] = {
    val state = if (!owner.isPersisted)
      new TransientO2MState(owner, transientRel, sqrlRelOf, fk)
    else
      new PersistentO2MState(owner, sqrlRelOf(owner), fk)

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
  val fk: M => ForeignKey[_])
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
    new PersistentO2MState[O, M](owner, squerylO2M, fk)
  }
}

private class PersistentO2MState[O <: Entity[O], M <: Entity[M]](
  val owner: O,
  val rel: SO2M[M],
  val fk: M => ForeignKey[_])
    extends OneToManyState[M] with PersistentStateLike[M] {

  private[handle] def -=(other: M): Unit = {
    val othersTable = other.table
    val othersFk = fk(other)

    if (othersFk.isOptional) {
      val optFk = othersFk.asInstanceOf[ForeignKey[Option[_]]]
      optFk.set(None)
      othersTable.update(other)
    } else {
      othersTable.deleteWhere(_.id === other.id)
    }
  }
}
