package org.chipmunk.entity.relation

import org.chipmunk.entity.Entity

trait RelationHandle[O <: Entity[_]] {
  self: Relation[O] =>

  def persist(): Unit
}
