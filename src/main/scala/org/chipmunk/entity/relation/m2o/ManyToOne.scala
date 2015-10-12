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

  protected final def isOwningSide: Boolean = false
}
