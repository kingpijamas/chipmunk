package org.chipmunk.entity.relation

import org.chipmunk.entity.Entity
import org.squeryl.dsl.{ OneToMany => SO2M }
import scala.collection.generic.Growable

object OneToMany {
  type SOneToMany[O] = SO2M[O]
}

trait OneToMany[O <: Entity[_]] extends Relation[O] with Growable[O] {
  final type SRel = OneToMany.SOneToMany[O]

  def +=(other: O): this.type = {
    toSqueryl.associate(other)
    this
  }

  def clear(): Unit = { toSqueryl.deleteAll }

  protected final def isOwningSide: Boolean = true
}
