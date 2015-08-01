package org.chipmunk.persistent.relation

import org.chipmunk.persistent.Entity
import org.squeryl.dsl.{ ManyToMany => SM2M }

object ManyToMany {
  type SManyToMany[O <: Entity[_]] = SM2M[O, Association2]
}

trait ManyToMany[O <: Entity[_]] extends Relation[O] {
  final type SRel = ManyToMany.SManyToMany[O]

  def add(other: O): Unit = { query.associate(other) }
  def add(others: O*): Unit = { others foreach { add(_) } }

  def remove(other: O): Unit = { query.dissociate(other) }
  def remove(others: O*): Unit = { others foreach { remove(_) } }

  def removeAll(): Unit = { query.dissociateAll }
}
