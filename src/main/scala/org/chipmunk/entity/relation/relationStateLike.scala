package org.chipmunk.entity.relation

import org.chipmunk.entity.Entity
import org.squeryl.Query

trait RelationStateLike[O <: Entity[_]] {
  type SRel <: Query[O]

  def isDirty: Boolean

  def persist(): RelationStateLike[O]

  def isTransient: Boolean

  def rel: SRel
}

private[relation] trait TransientStateLike[O <: Entity[_]]
    extends RelationStateLike[O] {
  final def isTransient: Boolean = true

  final def isDirty: Boolean = !rel.isEmpty
}

private[relation] trait PersistentStateLike[O <: Entity[_]]
    extends RelationStateLike[O] {
  final def isDirty: Boolean = false

  final def isTransient: Boolean = false

  final def persist(): this.type = this
}
