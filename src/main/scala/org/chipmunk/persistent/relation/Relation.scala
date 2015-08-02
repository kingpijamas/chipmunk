package org.chipmunk.persistent.relation

import org.squeryl.Query
import org.chipmunk.persistent.Entity

trait Relation[O <: Entity[_]] extends Iterable[O] {
  type SRel <: Query[O]
  //  def add(other: O): Unit
  //  def remove(other: O): Unit

  def removeAll(): Unit

  def toSqueryl: SRel

  def iterator: Iterator[O] = toSqueryl.iterator
}
