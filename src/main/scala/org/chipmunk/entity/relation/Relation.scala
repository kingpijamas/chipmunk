package org.chipmunk.entity.relation

import org.squeryl.Query
import org.chipmunk.entity.Entity

trait Relation[O <: Entity[_]] extends Iterable[O] {
  type SRel <: Query[O]

  def +=(other: O): this.type

  //  def remove(other: O): Unit

  def clear(): Unit

  def toSqueryl: SRel

  def iterator: Iterator[O] = toSqueryl.iterator

  protected def isOwningSide: Boolean
}
