package org.chipmunk.entity.relation.handle

import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.ManyToOne
import org.chipmunk.entity.relation.ManyToOne.SManyToOne
import org.chipmunk.entity.relation.mock
import scala.annotation.meta.field

object ManyToOneHandle {
  def apply[O <: Entity[_]](
    transient: Boolean,
    actualRel: SManyToOne[O],
    transientRel: SManyToOne[O] = mock.ManyToOne[O]())
  : ManyToOneHandle[O] = {
    val state = if (transient)
      new TransientM2OState(actualRel, transientRel)
    else
      new PersistentM2OState(actualRel)

    new ManyToOneHandle(new TransientM2OState[O](actualRel, transientRel))
  }
}

class ManyToOneHandle[O <: Entity[_]] private[handle] (
  private[this] var state: ManyToOneState[O])
    extends RelationHandle[O] with ManyToOne[O] {

  def persist(): Unit = { state = state.persist() }

  def toSqueryl: SManyToOne[O] = state.rel
}

sealed trait ManyToOneState[O <: Entity[_]] extends RelationStateLike[O] {
  final type SRel = ManyToOne.SManyToOne[O]

  override def persist(): ManyToOneState[O]
}

private class TransientM2OState[O <: Entity[_]](
  val actualRel: SManyToOne[O],
  val rel: SManyToOne[O])
    extends ManyToOneState[O] with TransientStateLike[O] {

  def persist(): PersistentM2OState[O] = {
    // FIXME: far from ideal, but will work if the O2M on
    // the other side is used correctly
    if (isDirty) {
      //TODO: this line below assumes a non-standard behavior only mock.ManyToOne has!
      rel foreach { _.persistBody() }
    }
    new PersistentM2OState[O](actualRel)
  }
}

private class PersistentM2OState[O <: Entity[_]](
  val rel: SManyToOne[O])
    extends ManyToOneState[O] with PersistentStateLike[O]
