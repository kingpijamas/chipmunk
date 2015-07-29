package org.chipmunk.persistent.relation.impl

import scala.language.implicitConversions
import org.chipmunk.persistent.Entity
import org.chipmunk.persistent.relation.Association2
import org.chipmunk.persistent.relation.ManyToMany
import org.squeryl.dsl.{ OneToMany => SOneToMany }
import org.chipmunk.persistent.relation.OneToMany

object OneToManyImpl {
  implicit def wrapSquerylO2M[O <: Entity[_]](
    o2m: SOneToMany[O]): OneToManyImpl[O] = OneToManyImpl(o2m)

  def apply[O <: Entity[_]](o2m: SOneToMany[O]): OneToManyImpl[O] =
    new OneToManyImpl(o2m)
}

class OneToManyImpl[O <: Entity[_]](o2m: SOneToMany[O])
    extends OneToMany[O] {
  def add(other: O): Unit = { o2m.associate(other) }
  def removeAll(): Unit = { o2m.deleteAll }
  def query: SOneToMany[O] = o2m
}
