package org.chipmunk.entity.relation

import org.chipmunk.entity.Entity
import org.squeryl.dsl.{ ManyToMany => SM2M }
import scala.collection.generic.Growable
import scala.collection.generic.Shrinkable

object ManyToMany {
  type SManyToMany[O <: Entity[_]] = SM2M[O, Association2]
}

trait ManyToMany[O <: Entity[_]]
    extends Relation[O] with Growable[O] with Shrinkable[O] {
  final type SRel = ManyToMany.SManyToMany[O]

  def +=(other: O): this.type = {
    val squerylRel = toSqueryl
    if (isOwningSide) {
      squerylRel.associate(other)
    } else {
      squerylRel.assign(other)
    }
    this
  }

  def -=(other: O): this.type = {
    toSqueryl.dissociate(other)
    this
  }

  def clear(): Unit = { toSqueryl.dissociateAll }
}
