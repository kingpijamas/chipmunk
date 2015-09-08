package org.chipmunk.entity.relation.m2o

import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.Relation
import org.squeryl.dsl.{ ManyToOne => SM2O }
import org.chipmunk.entity.relation.m2o.ManyToOne.SManyToOne

object ManyToOne {
  type SManyToOne[O <: Entity[_]] = SM2O[O]
}

trait ManyToOne[O <: Entity[_]] extends Relation[O] {
  final type SRel = SManyToOne[O]

  def +=(other: O): this.type = {
    toSqueryl.assign(other)
    this
  }

  def clear(): Unit = { toSqueryl.delete }

  protected final def isOwningSide: Boolean = false
}
