package org.chipmunk.persistent.relation

import org.chipmunk.persistent.Entity
import org.squeryl.dsl.{ OneToMany => SOneToMany }

trait OneToMany[O <: Entity[_]] extends Relation[O, SOneToMany[O]] {
  def add(others: O*): Unit = { others foreach { add(_) } }
//  def remove(others: O*): Unit
}
