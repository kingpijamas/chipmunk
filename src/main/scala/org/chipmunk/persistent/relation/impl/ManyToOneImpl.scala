package org.chipmunk.persistent.relation.impl

import scala.language.implicitConversions

import org.chipmunk.persistent.Entity
import org.chipmunk.persistent.relation.ManyToOne
import org.squeryl.dsl.{ ManyToOne => SManyToOne }

object ManyToOneImpl {
  implicit def wrapSquerylM2O[O <: Entity[_]](
    m2o: SManyToOne[O]): ManyToOneImpl[O] = ManyToOneImpl(m2o)

  def apply[O <: Entity[_]](m2o: SManyToOne[O]): ManyToOneImpl[O] =
    new ManyToOneImpl(m2o)
}

class ManyToOneImpl[O <: Entity[_]](m2o: SManyToOne[O])
    extends ManyToOne[O] {
//  def add(other: O): Unit = { m2o.associate(other) }
//  def remove(other: O): Unit = { m2o.dissociate(other) }
  def removeAll(): Unit = { m2o.delete }

  def query: SManyToOne[O] = m2o
}
