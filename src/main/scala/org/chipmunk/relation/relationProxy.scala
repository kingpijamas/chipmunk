package org.chipmunk.relation

import org.chipmunk.entity.Entity
import org.squeryl.Query

trait RelationProxy[O <: Entity[_]] {
  self: Relation[O] =>

  def isDirty: Boolean

  def persist(): RelationProxy[O]

  final def toSqueryl: SRel = rel

  protected[this] def rel: SRel
}

protected[relation] trait TransientLike[O <: Entity[_]]
    extends RelationProxy[O] {
  self: Relation[O] =>

  final def isDirty: Boolean = !rel.isEmpty
}

protected[relation] trait PersistentLike[O <: Entity[_]]
    extends RelationProxy[O] {
  self: Relation[O] =>

  final def isDirty: Boolean = false

  final def persist(): this.type = this
}
