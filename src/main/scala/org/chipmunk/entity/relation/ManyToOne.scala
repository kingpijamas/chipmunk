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
  def add(other: O): Unit = { toSqueryl.assign(other) }

  def removeAll(): Unit = { toSqueryl.delete }
}
