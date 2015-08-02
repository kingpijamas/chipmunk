package org.chipmunk.persistent.relation

import org.chipmunk.persistent.Entity
import org.squeryl.dsl.{ ManyToOne => SM2O }
import org.chipmunk.persistent.relation.ManyToOne.SManyToOne

object ManyToOne {
  type SManyToOne[O <: Entity[_]] = SM2O[O]
}

trait ManyToOne[O <: Entity[_]] extends Relation[O] {
  final type SRel = SManyToOne[O]

  def removeAll(): Unit = { toSqueryl.delete }
}
