package org.chipmunk.persistent.relation

import org.chipmunk.persistent.Entity
import org.squeryl.dsl.{ ManyToMany => SManyToMany }

trait ManyToMany[O <: Entity[_]] extends Relation[O, SManyToMany[O, Association2]] {
  def add(others: O*): Unit

  def remove(others: O*): Unit
}
