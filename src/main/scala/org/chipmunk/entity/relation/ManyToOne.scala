package org.chipmunk.entity.relation

import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.ManyToOne.SManyToOne
import org.squeryl.dsl.{ ManyToOne => SM2O }

object ManyToOne {
  type SManyToOne[O <: Entity[_]] = SM2O[O]
}

trait ManyToOne[O <: Entity[_]] extends Relation[O] {
  final type SRel = SManyToOne[O]
  // FIXME: this should actually add the element
  def +=(other: O): this.type = {
    toSqueryl.assign(other)
    this
  }

  def clear(): Unit = { toSqueryl.delete }

  protected final def isOwningSide: Boolean = false
}
