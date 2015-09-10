package org.chipmunk.entity.relation

import org.chipmunk.entity.Entity
import scala.annotation.meta.field

abstract class RelationHandle[O <: Entity[_]](
  private[relation] var state: RelationStateLike[O])
    extends Relation[O] {
  def persist(): Unit = { state = state.persist() }
}
