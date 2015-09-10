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
  def apply[O <: Entity[_]](
    owner: Entity[_],
    actualRel: SManyToOne[O],
    transientRel: SManyToOne[O] = mock.ManyToOne[O]()): ManyToOneHandle[O] = {
    val state = if (!owner.isPersisted)
      new TransientM2OState(actualRel, transientRel)
    else
      new PersistentM2OState(actualRel)

    new ManyToOneHandle(state)
  }
}

class ManyToOneHandle[O <: Entity[_]] private[m2o](state: ManyToOneState[O])
    extends RelationHandle[O](state) with ManyToOne[O] {
  def toSqueryl: SRel = state.rel
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
