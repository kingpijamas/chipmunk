package org.chipmunk.entity.relation.handle

import org.chipmunk.entity.relation.Relation
import org.chipmunk.entity.Entity

trait RelationHandle[O <: Entity[_]] {
  self: Relation[O] =>

  def persist(): Unit
}
