package org.chipmunk.relation

import org.chipmunk.entity.Entity
import org.squeryl.dsl.{ OneToMany => SO2M }

object OneToMany {
  type SOneToMany[O] = SO2M[O]
}

trait OneToMany[O <: Entity[_]] extends Relation[O] {
  final type SRel = OneToMany.SOneToMany[O]

  def add(other: O): Unit = { toSqueryl.associate(other) }
  def add(others: O*): Unit = { others foreach { add(_) } }

  def removeAll(): Unit = { toSqueryl.deleteAll }
  //  def remove(others: O*): Unit
}
