package org.chipmunk.entity.relation.m2o

import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.m2o.ManyToOne.SManyToOne
import org.chipmunk.entity.relation.PersistentStateLike
import org.chipmunk.entity.relation.RelationStateLike
import org.chipmunk.entity.relation.TransientStateLike
import org.chipmunk.test.{ relation => mock }
import scala.annotation.meta.field
import org.chipmunk.entity.relation.RelationHandle

object ManyToOneHandle {
  def apply[M <: Entity[M], O <: Entity[O]](
    owner: M,
    squerylRelOf: M => SManyToOne[O],
    transientRel: SManyToOne[O] = mock.ManyToOne[O]())
  : ManyToOneHandle[O] = {
    val state = if (!owner.isPersisted)
      new TransientM2OState(owner, squerylRelOf, transientRel)
    else
      new PersistentM2OState(owner, squerylRelOf)

    new ManyToOneHandle(state)
  }
}

class ManyToOneHandle[O <: Entity[O]] private[m2o] (
  state: ManyToOneState[O])
    extends RelationHandle[O](state) with ManyToOne[O] {

  def +=(other: O): this.type = {
    state += other
    this
  }

  def clear(): Unit = {  state.clear() }

  def toSqueryl: SRel = state.rel
}

sealed trait ManyToOneState[O <: Entity[_]] extends RelationStateLike[O] {
  final type SRel = ManyToOne.SManyToOne[O]

  def +=(other: O): Unit = { rel.assign(other) }

  def clear(): Unit = { rel.delete }

  override def persist(): ManyToOneState[O]
}

private class TransientM2OState[M <: Entity[M], O <: Entity[O]](
  val owner: M,
  val squerylRelOf: M => SManyToOne[O],
  val rel: SManyToOne[O])
    extends ManyToOneState[O] with TransientStateLike[O] {

  def persist(): PersistentM2OState[M, O] = {
    // FIXME: far from ideal, but will work if the O2M on
    // the other side is used correctly
    if (isDirty) {
      //TODO: this line below assumes a non-standard behavior only 
      //mock.ManyToOne has!
      rel foreach { _.persistBody() }
    }
    new PersistentM2OState(owner, squerylRelOf)
  }
}

private class PersistentM2OState[M <: Entity[M], O <: Entity[O]](
  val owner: M,
  val squerylRelOf: M => SManyToOne[O])
    extends ManyToOneState[O] with PersistentStateLike[O] {

  // squeryl's m2o must be kept up to date
  private[this] var _rel: SManyToOne[O] = squerylRelOf(owner)

  override def +=(other: O): Unit = {
    super.+=(other)
    _rel = squerylRelOf(owner)
  }

  override def clear(): Unit = {
    super.clear()
    _rel = squerylRelOf(owner)
  }

  def rel: SManyToOne[O] = _rel
}
