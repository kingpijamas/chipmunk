package org.chipmunk.persistent.relation

import org.squeryl.Query
import org.chipmunk.persistent.Entity

trait Relation[O <: Entity[_], SRel <: Query[O]] extends Iterable[O] {
//  def add(other: O): Unit

//  def remove(other: O): Unit
  def removeAll(): Unit

  def query: SRel
}
