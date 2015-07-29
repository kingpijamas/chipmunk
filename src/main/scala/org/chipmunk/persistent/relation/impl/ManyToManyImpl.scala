package org.chipmunk.persistent.relation.impl

import scala.language.implicitConversions

import org.chipmunk.persistent.Entity
import org.chipmunk.persistent.relation.Association2
import org.chipmunk.persistent.relation.ManyToMany
import org.squeryl.dsl.{ ManyToMany => SManyToMany }

object ManyToManyImpl {
  implicit def wrapSquerylM2M[O <: Entity[_]](
    m2m: SManyToMany[O, Association2]): ManyToManyImpl[O] =
    ManyToManyImpl(m2m)

  def apply[O <: Entity[_]](
    m2m: SManyToMany[O, Association2]): ManyToManyImpl[O] =
    new ManyToManyImpl(m2m)
}

class ManyToManyImpl[O <: Entity[_]](m2m: SManyToMany[O, Association2])
    extends ManyToMany[O] {
  def add(other: O): Unit = { m2m.associate(other) }
  def remove(other: O): Unit = { m2m.dissociate(other) }
  def query: SManyToMany[O, Association2] = m2m
}
